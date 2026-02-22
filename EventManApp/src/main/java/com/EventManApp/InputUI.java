package com.EventManApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.time.LocalDate;
import java.util.regex.Pattern;
import java.time.format.DateTimeParseException;

import com.EventManApp.lib.DebugUtil;
import com.EventManApp.lib.TokenizedString;

public class InputUI {
    private final Scanner scanner;

    public InputUI() {
        this.scanner = new Scanner(System.in);
    }

    public String getUserInput(String argName, JSONObject argTypeAttr) {
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
    public String getDefaultValue(String baseType, String extraWord) {
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

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void waitForKeyPress() {
        // Wait for the user to press Enter before proceeding
        showMessage("Press Enter to continue...");
        scanner.nextLine(); // Wait for a key press
    }
}
