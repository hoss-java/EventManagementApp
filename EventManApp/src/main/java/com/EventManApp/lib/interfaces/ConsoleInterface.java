package com.EventManApp.lib.interfaces;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.Duration;
import java.util.regex.Pattern;

import java.util.Scanner;

import com.EventManApp.MenuCallback;

public class ConsoleInterface {
    private final Scanner scanner;
    private final MenuCallback callback;

    public ConsoleInterface(MenuCallback callback) {
        this.scanner = new Scanner(System.in);
        this.callback = callback;
    }

    public JSONObject executeCommands(JSONObject commands) {
        JSONObject selectedCommand = new JSONObject();
        navigateCommands(commands.getJSONArray("commands"), selectedCommand, "Exit");
        return selectedCommand;
    }

    private void navigateCommands(JSONArray commandsArray, JSONObject selectedCommand, String backCommand) {
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

            selectedCommand.put("id", commandName);

            if (command.has("args")) {
                JSONObject args = new JSONObject();
                JSONObject arguments = command.getJSONObject("args");

                for (String argName : arguments.keySet()) {
                    JSONObject argType = arguments.getJSONObject(argName);
                    String argValue = getUserInput(argName, argType);
                    args.put(argName, argValue);
                }
                selectedCommand.put("args", args);
            }

            // Check for nested commands
            if (command.has("commands")) {
                navigateCommands(command.getJSONArray("commands"), selectedCommand, "Back");
                if (!commandName.equals(selectedCommand.getString("id"))){
                    String response = callback.onMenuItemSelected("ConsoleInterface",selectedCommand.toString());
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
        String argType = argTypeAttr.optString("type", "str");
        String argModifier = argTypeAttr.optString("modifier", "user");
        boolean argMandatory = argTypeAttr.optBoolean("mandatory", true);
        String argDefault = argTypeAttr.optString("default", "");

        // Default behavior
        while (true) {
            if (argModifier.equals("user")){
                System.out.print("Enter " + argName + " (" + argType + "): ");
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

            case "positiveInt":
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
