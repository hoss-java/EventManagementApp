package com.EventManApp.lib;

import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;

public class JSONHelper {

    public JSONHelper() {
    }

    // This method must be defined within the Main class
    public static JSONObject loadJsonFromFile(String fileName) {
        try (InputStream inputStream = JSONHelper.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("File not found: " + fileName);
            }
            // Read the input stream into a string
            String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            // Create JSONObject using LinkedHashMap to preserve order
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            JSONObject jsonObject = new JSONObject(map);
            return new JSONObject(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void traverseAndPrint(JSONObject jsonObject, String parentKey) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            String fullKey = parentKey.isEmpty() ? key : parentKey + "." + key;

            if (value instanceof JSONObject) {
                // Recursive call for nested JSONObject
                traverseAndPrint((JSONObject) value, fullKey);
            } else {
                // Print the key and value
                System.out.println(fullKey + ": " + value);
            }
        }
    }

    public static Map<String, String> createEventFields(JSONObject json) {
        Map<String, String> eventFields = new HashMap<>();

        // Iterate through the keys of the JSONObject
        for (String key : json.keySet()) {
            // Get value as string and put it in the map
            eventFields.put(key, json.get(key).toString());
        }

        // Validate required fields if necessary
        validateRequiredFields(eventFields);

        return eventFields;
    }

    private static void validateRequiredFields(Map<String, String> eventFields) {
        if (!eventFields.containsKey("uid")) {
            throw new IllegalArgumentException("Field 'uid' is required.");
        }
        if (!eventFields.containsKey("title")) {
            throw new IllegalArgumentException("Field 'title' is required.");
        }
        // Add validation for additional required fields if necessary
    }
}