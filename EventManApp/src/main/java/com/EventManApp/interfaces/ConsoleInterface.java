package com.EventManApp.interfaces;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.EventManApp.BaseInterface;
import com.EventManApp.ResponseCallbackInterface;
import com.EventManApp.lib.DebugUtil;
import com.EventManApp.lib.TokenizedString;

public class ConsoleInterface extends BaseInterface {
    private final Scanner scanner;

    public ConsoleInterface(ResponseCallbackInterface callback) {
        super(callback);
        this.scanner = new Scanner(System.in);
    }

    @Override
    public JSONObject executeCommands(JSONObject commands) {
        JSONObject peyload = new JSONObject();
        navigateCommands("root",commands.getJSONArray("commands"), peyload, "Exit");
        return peyload;
    }

    private void navigateCommands(String rootID,JSONArray commandsArray, JSONObject peyload, String backCommand) {
        while (true) {
            String commandName = displayCommandsAndGetChoice(commandsArray, backCommand);
            if (commandName.equals(backCommand)) {
                return; // Exit to the previous menu
            }
            
            JSONObject command = findCommandByName(commandsArray, commandName);
            if (command == null) {
                showMessage("Invalid choice, please try again.");
                continue;
            }

            peyload.put("identifier", rootID);

            if (command.has("args")) {
                JSONObject args = new JSONObject();
                JSONObject arguments = command.getJSONObject("args");

                for (String argName : arguments.keySet()) {
                    JSONObject argType = arguments.getJSONObject(argName);
                    String argValue = getUserInput(argName, argType);
                    String argField = argType.optString("field", argName);
                    args.put(argField, argValue);
                }
                JSONObject peyloadCommand = new JSONObject();
                peyloadCommand.put("id", command.getString("action"));
                peyloadCommand.put("data", args);
                peyloadCommand.put("args", arguments);

                JSONArray payloadCommandsArray = new JSONArray();
                payloadCommandsArray.put(peyloadCommand);
                // Put the commands list into the JSON object
                peyload.put("commands", payloadCommandsArray);                
            }

            // Check for nested commands
            // need to add try catch
            if (command.has("commands")) {
                navigateCommands(command.getString("id"), command.getJSONArray("commands"), peyload, "Back");
                if (!rootID.equals(peyload.getString("identifier"))){
                    String response = callback.ResponseHandler("ConsoleInterface",peyload.toString());
                    System.out.println("Response: " + response); // Print the JSON response
                    // Wait for the user to press Enter before proceeding
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine(); // Wait for a key press
                }
            } else {
                break; // Exit the while loop if a valid command is processed
            }
        }
    }

    private JSONObject findCommandByName(JSONArray commandsArray, String commandName) {
        for (int i = 0; i < commandsArray.length(); i++) {
            JSONObject command = commandsArray.getJSONObject(i);
            if (command.getString("id").equals(commandName)) {
                return command;
            }
        }
        return null; // Command not found
    }

    public String displayCommandsAndGetChoice(JSONArray commandsArray, String backCommand) {
        clearConsole(); // Clear the console before displaying commands
        System.out.println("Available Commands:");
        int index = 1;
        int commandCount = commandsArray.length() + (backCommand != null ? 1 : 0); // Add 1 for back command
        String[] commandKeys = new String[commandCount];

        // Print each command with a corresponding number
        for (int i = 0; i < commandsArray.length(); i++) {
            JSONObject cmd = commandsArray.getJSONObject(i);
            System.out.println("  "+index + ". " + cmd.getString("description")+" ("+cmd.getString("id")+")");
            commandKeys[index - 1] = cmd.getString("id"); // Store command key
            index++;
        }

        // Add the back command as the last item
        if (backCommand != null) {
            System.out.println("  "+index + ". " + backCommand);
            commandKeys[index - 1] = backCommand; // Store back command
        }

        String selectedCommand = null;

        // Ask for a valid choice
        while (selectedCommand == null) {
            System.out.print("\nPlease type the number of your choice: ");
            int choice;

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= commandCount) {
                    selectedCommand = commandKeys[choice - 1]; // Convert number to command name
                } else {
                    showMessage("Invalid choice. Please choose a number between 1 and " + commandCount + ".");
                }
            } catch (NumberFormatException e) {
                showMessage("Invalid input. Please enter a number.");
            }
        }

        return selectedCommand;
    }

    private String getUserInput(String argName, JSONObject argTypeAttr) {
        // Check if the type contains an extra word
        String argField = (new TokenizedString(argTypeAttr.optString("field", argName),"@")).getPart(-1);
        String argDescription = argTypeAttr.optString("description", argField);
        String argType = (new TokenizedString(argTypeAttr.optString("type", "str"),"@")).getPart(-1);
        String argModifier = argTypeAttr.optString("modifier", "user");
        boolean argMandatory = argTypeAttr.optBoolean("mandatory", true);
        String argDefault = argTypeAttr.optString("default", "");

        // Default behavior
        while (true) {
            if (argModifier.equals("user")){
                System.out.print("Enter " + argDescription + (argMandatory ? "*" : "") +" (" + argType + "): ");
                String input = scanner.nextLine().trim();

                if (input.equals("") && argMandatory == false){
                    return argDefault;    
                    }

                if (isValid(input, argType)) {
                    return input;
                } else {
                    showMessage("Invalid " + argType + ". Please try again.");
                }
            }
            else{
                return argDefault;
            }
        }
    }

    // Auxiliary function to retrieve default values based on type and extra word
    private String getDefaultValue(String baseType, String extraWord) {
        switch (baseType) {
            case "date":
                if ("default".equals(extraWord)) {
                    return getCurrentDate(); // Implement this function to return the current date
                }
                break;
            case "time":
                if ("default".equals(extraWord)) {
                    return getCurrentTime(); // New function for current time
                }
                break;
            // Add more cases as needed for other base types
            // Handle other base types if necessary
        }
        return null; // Return null if no valid default value is found
    }

    private String getCurrentDate() {
        java.util.Date date = new java.util.Date();
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    private String getCurrentTime() {
        java.util.Date date = new java.util.Date();
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("HH:mm:ss");
        return formatter.format(date);
    }

    private boolean isValid(String input, String argType) {
        switch (argType) {
            case "str":
                if (input.isEmpty()) {
                    System.out.println("Hint: Input should be a non-empty string.");
                    return false;
                }
                return true;

            case "int":
                if (!input.matches("-?\\d+")) {
                    System.out.println("Hint: Input should be a valid integer, which can be positive, negative, or zero. Example: 123, -45, or 0.");
                    return false;
                }
                return true;

            case "unsigned":
                if (!input.matches("\\d+")) {
                    System.out.println("Hint: Input should be a positive integer greater than zero. Example: 1, 100, or 456.");
                    return false; // Matches only positive integers.
                }
                return true;

            case "date":
                if (!isValidDate(input)) {
                    System.out.println("Hint: Input should be a valid date in the format YYYY-MM-DD. Example: 2026-02-15.");
                    return false;
                }
                return true;

            case "time":
                if (!isValidTime(input)) {
                    System.out.println("Hint: Input should be a valid time in the format HH:mm (24-hour format). Example: 14:30.");
                    return false;
                }
                return true;

            case "duration":
                if (!isValidDuration(input)) {
                    System.out.println("Hint: Input should be a valid duration format. Example: 1h 30m (for 1 hour and 30 minutes).");
                    return false;
                }
                return true;

            default:
                System.out.println("Hint: Unknown argument type: " + argType);
                return false;
        }
    }

    // Example methods for additional validations
    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true; // Successful parsing means the date is valid.
        } catch (DateTimeParseException e) {
            return false; // Exception thrown means invalid date format.
        }
    }

    private boolean isValidTime(String time) {
        return time.matches("([01]\\d|2[0-3]):[0-5]\\d"); // Matches HH:mm (24-hour format)
    }

    private boolean isValidDuration(String duration) {
        return Pattern.matches("\\d+h \\d+m", duration); // Matches "Xh Ym" format
    }

    private void showMessage(String message) {
        System.out.println(message);
    }

    private void clearConsole() {
        // Clear the console depending on the operating system
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J"); // For Unix/Linux/Mac
                System.out.flush();
            }
        } catch (Exception e) {
            // Handle exceptions accordingly
        }
    }

    private static void printJson(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        // Print the JSON with indentation for readability
        String formattedJson = jsonObject.toString(4); // Indent with 4 spaces
        System.out.println("JSON Response:\n" + formattedJson);
    }

    public void close() {
        scanner.close();
    }
}
