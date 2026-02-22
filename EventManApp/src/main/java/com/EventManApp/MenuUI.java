package com.EventManApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.EventManApp.lib.DebugUtil;

public class MenuUI {
    private final Scanner scanner;
    private String menuTitle = "Available Commands";

    public MenuUI(String menuTitle) {
        this.menuTitle = menuTitle;
        this.scanner = new Scanner(System.in);
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
        System.out.println(this.menuTitle+":");
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
            peyload.put("command",command);

            // Check for nested commands
            // need to add try catch
            if (command.has("commands")) {
                navigateCommands(command.getString("id"), command.getJSONArray("commands"), peyload, "Back");
                if (!rootID.equals(peyload.getString("identifier"))){
                    return;
                }
            } else {
                break; // Exit the while loop if a valid command is processed
            }
        }
    }
    
    public JSONObject displayMenu(JSONObject commands) {
        JSONObject peyload = new JSONObject();
        navigateCommands("root",commands.getJSONArray("commands"), peyload, "Exit");
        return peyload;
    }
}
