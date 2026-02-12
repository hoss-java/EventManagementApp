package com.EventManApp.storages;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.EventManApp.KVObjectField;
import com.EventManApp.KVSubject;
import com.EventManApp.KVSubjectStorage;
import com.EventManApp.lib.DebugUtil;

public class FileKVSubjectStorage implements KVSubjectStorage {
    private File file;

    public FileKVSubjectStorage(File file) throws IOException {
        this.file = file;

        // If the file does not exist, create a new one
        if (!file.exists()) {
            boolean created = file.createNewFile();
            if (!created) {
                throw new IOException("Failed to create new file: " + file.getAbsolutePath());
            }
        }
    }

    @Override
    public void addKVSubject(KVSubject kvSubject) {
        JSONArray jsonArray = loadFromFile();

        // Check if the subject already exists based on identifier
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject existingSubject = jsonArray.getJSONObject(i);
            if (existingSubject.getString("identifier").equals(kvSubject.getIdentifier())) {
                return; // Subject already exists, do nothing
            }
        }

        // Add the new subject
        jsonArray.put(kvSubject.toJSON()); // Convert the KVSubject to JSON and add it

        // Save all subjects back to the file
        saveToFile(jsonArray);
    }

    @Override
    public boolean removeKVSubject(KVSubject kvSubject) {
        JSONArray jsonArray = loadFromFile();
        boolean removed = false;

        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getJSONObject(i).getString("identifier").equals(kvSubject.getIdentifier())) {
                jsonArray.remove(i); // Remove the specific subject
                removed = true;
                break;
            }
        }

        if (removed) {
            saveToFile(jsonArray); // Save updated list back to file
        }
        return removed;
    }

    @Override
    public KVSubject getKVSubject(String identifier) {
        JSONArray jsonArray = loadFromFile();

        if (jsonArray == null || jsonArray.length() == 0) {
            // Handle the case where the JSON array is empty or null
            return null; // Not found, as there are no records to search through
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getString("identifier").equals(identifier)) {
                return deserializeKVSubject(jsonObject); // Return the KVSubject from JSON
            }
        }
        return null; // Not found
    }

    @Override
    public List<KVSubject> getAllKVSubjects() {
        List<KVSubject> subjects = new ArrayList<>();
        JSONArray jsonArray = loadFromFile();

        if (jsonArray == null || jsonArray.length() == 0) {
            // Handle the case where the JSON array is empty or null
            return null; // Not found, as there are no records to search through
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            subjects.add(deserializeKVSubject(jsonObject)); // Deserialize and add to list
        }
        return subjects;
    }

    @Override
    public int countKVSubjects() {
        if (!file.exists() || file.length() == 0) {
            return 0; // Return 0 if the file does not exist or is empty
        }

        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.readLine() != null) {
                lineCount++; // Increment the line count for each line read
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions
        }
        return lineCount; // Return the total line count
    }

    // Method to save subjects to a file
    private void saveToFile(JSONArray jsonArray) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.write(jsonArray.toString()); // Write the JSON array to the file
        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions
        }
    }

    // Method to read subjects from the file
    private JSONArray loadFromFile() {
        StringBuilder jsonStringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStringBuilder.append(line); // Read file content
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions
        }

        // Return an empty JSONArray if the string is empty
        if (jsonStringBuilder.length() == 0) {
            return new JSONArray(); // Return empty array if no data is found
        }

        // Parse the loaded content to JSON format
        return new JSONArray(jsonStringBuilder.toString());
    }

    // Method to deserialize a JSONObject to a KVSubject
    private KVSubject deserializeKVSubject(JSONObject jsonObject) {
        String identifier = jsonObject.getString("identifier");
        String description = jsonObject.getString("description");
        KVSubject kvSubject = new KVSubject(identifier, description);

        // Restore the fieldTypeMap from JSON
        JSONArray fieldsArray = jsonObject.getJSONArray("fieldTypeMap");
        for (int j = 0; j < fieldsArray.length(); j++) {
            JSONObject fieldObject = fieldsArray.getJSONObject(j);
            KVObjectField field = new KVObjectField(
                fieldObject.getString("field"),
                fieldObject.getString("type"),
                fieldObject.getBoolean("mandatory"),
                fieldObject.getString("modifier"),
                fieldObject.getString("defaultValue")
            );
            kvSubject.getFieldTypeMap().put(field.getField(), field); // Add to the fieldTypeMap
        }

        // Restore the nextId value
        kvSubject.setNextId(jsonObject.getInt("nextId")); // Set the nextId

        return kvSubject; // Return the fully constructed KVSubject
    }

    @Override
    public void close() {
    }
}
