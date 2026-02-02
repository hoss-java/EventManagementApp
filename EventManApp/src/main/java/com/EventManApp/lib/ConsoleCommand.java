package com.EventManApp.lib;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Scanner;

public class ConsoleCommand {
    private final Scanner scanner;

    public ConsoleCommand() {
        this.scanner = new Scanner(System.in);
    }

    public JSONObject executeCommands(JSONObject commands) {
        JSONObject selectedCommand = new JSONObject();
        navigateCommands(commands.getJSONArray("commands"), selectedCommand, null);
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
                    String argType = arguments.getString(argName);
                    String argValue = getUserInput(argName, argType);
                    args.put(argName, argValue);
                }
                selectedCommand.put("args", args);
            }

            // Check for nested commands
            if (command.has("commands")) {
                navigateCommands(command.getJSONArray("commands"), selectedCommand, "Back");
                if (!commandName.equals(selectedCommand.getString("id"))){
                    break;
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

    private String getUserInput(String argName, String argType) {
        while (true) {
            System.out.print("Enter " + argName + " (" + argType + "): ");
            String input = scanner.nextLine().trim();

            if (isValid(input, argType)) {
                return input;
            } else {
                showMessage("Invalid " + argType + ". Please try again.");
            }
        }
    }

    private boolean isValid(String input, String argType) {
        switch (argType) {
            case "str":
                return !input.isEmpty(); // Any non-empty string is valid.
            case "int":
                return input.matches("-?\\d+"); // Matches positive and negative integers.
            case "date":
                return isValidDate(input); // Check if the date is in the right format.
            default:
                return false;
        }
    }

    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date); // Assuming the date is in ISO format (YYYY-MM-DD).
            return true;
        } catch (Exception e) {
            return false;
        }
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

    public void close() {
        scanner.close();
    }
}
