package com.EventManApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigManager {
    private static ConfigManager instance;
    private JSONObject configData;
    private String filePath;

    // Private constructor for singleton
    private ConfigManager(String filePath) {
        this.filePath = filePath;
        configData = new JSONObject();
        loadConfig();
    }

    // Public method to get the single instance of ConfigManager
    public static ConfigManager getInstance(String filePath) {
        if (instance == null) {
            instance = new ConfigManager(filePath);
        }
        return instance;
    }

    // Load configurations from a JSON file
    private void loadConfig() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            configData = new JSONObject(content);
        } catch (IOException | JSONException e) {
            System.out.println("No configuration file found to load.");
        }
    }

    // Save configurations to a JSON file
    public void saveConfig() {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(configData.toString(4)); // Pretty print with an indent of 4
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T castToGeneric(Object value) {
        return (T) value; // Unchecked cast
    }

    public <T> T getSetting(String section, String key, T defaultValue) {
        JSONObject sectionObj = getSection(section);

        if (sectionObj != null && sectionObj.has(key)) {
            Object value = sectionObj.get(key);
            return castToGeneric(value);
        }
        return defaultValue; // Return default if not found
    }

    public <T> void setSetting(String section, String key, T value) {
        JSONObject sectionObj = getSection(section);
        if (sectionObj == null) {
            sectionObj = new JSONObject();
            if (section != null) {
                configData.put(section, sectionObj);
            }
        }
        sectionObj.put(key, value); // Store value directly
        saveConfig(); // Save after setting
    }

    // Helper method to get a specific section
    private JSONObject getSection(String section) {
        if (section == null) {
            return configData; // Return the root config
        }

        String[] keys = section.split("\\.");
        JSONObject currentSection = configData;
        for (String key : keys) {
            if (currentSection.has(key)) {
                currentSection = currentSection.getJSONObject(key);
            } else {
                return null; // Section does not exist
            }
        }
        return currentSection;
    }
}





