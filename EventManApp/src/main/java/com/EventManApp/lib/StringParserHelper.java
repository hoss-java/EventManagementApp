package com.EventManApp.lib;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringParserHelper {
    private static Map<String, String> replacements = new HashMap<>();

    // Static block to initialize default replacements
    static {
        replacements.put("%DATE%", LocalDate.now().toString());
        replacements.put("%TIME%", LocalTime.now().toString());
    }

    // Constructor to initialize default replacements
    public StringParserHelper() {
    }

    // Static method to add a new replacement
    public static void addReplacement(String placeholder, String value) {
        replacements.put(placeholder, value);
    }

    // Static method to parse the input string
    public static String parseString(String input) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String placeholder = entry.getKey();
            String replacement = entry.getValue();
            input = input.replace(placeholder, replacement);
        }
        return input;
    }
}
