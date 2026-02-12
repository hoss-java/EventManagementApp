package com.EventManApp.storages;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.EventManApp.KVObject;
import com.EventManApp.KVObjectField;
import com.EventManApp.KVObjectStorage;
import com.EventManApp.lib.DebugUtil;

public class FileKVObjectStorage implements KVObjectStorage {
    private File storageDirectory;

    public FileKVObjectStorage(File storageDirectory) throws IOException {
        this.storageDirectory = storageDirectory;

        // Create the directory if it doesn't exist
        if (!storageDirectory.exists()) {
            boolean created = storageDirectory.mkdirs();
            if (!created) {
                throw new IOException("Failed to create directory: " + storageDirectory.getAbsolutePath());
            }
        }
    }

    private boolean ensureFileAndDirectoryExists(File file) {
        // Ensure the directory exists
        File directory = file.getParentFile();
        if (directory != null && !directory.exists()) {
            boolean dirCreated = directory.mkdirs(); // Create the directory, including any necessary parent directories
            if (!dirCreated) {
                System.err.println("Failed to create directory: " + directory.getAbsolutePath());
                return false; // Return false if the directory could not be created
            }
        }

        // Ensure the file exists
        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
                return created; // Return true if the file was created, false otherwise
            } catch (IOException e) {
                e.printStackTrace();
                return false; // Return false if an exception occurred
            }
        }
        return true; // Both directory and file already exist
    }

    @Override
    public void addKVObject(KVObject kvObject) {
        File file = getFileForIdentifier(kvObject.getIdentifier());

        // Ensure the file exists before proceeding
        if (!ensureFileAndDirectoryExists(file)) {
            System.err.println("Error: Could not create file: " + file.getAbsolutePath());
            return; // Stop execution if the file can't be created
        }

        // Proceed to write the KVObject to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(kvObject.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle additional errors if needed
        }
    }

    @Override
    public boolean removeKVObject(KVObject kvObject) {
        File file = getFileForIdentifier(kvObject.getIdentifier());
        List<KVObject> kvObjects = getKVObjects(kvObject.getIdentifier());
        boolean removed = kvObjects.removeIf(existingKVObject -> existingKVObject.getIdentifier().equals(kvObject.getIdentifier()));

        // If something was removed, rewrite the file
        if (removed) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (KVObject obj : kvObjects) {
                    writer.write(obj.toString());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return removed;
    }

    @Override
    public List<KVObject> getKVObjects(String identifier) {
        File file = getFileForIdentifier(identifier);

        // Ensure the file exists before proceeding
        if (!ensureFileAndDirectoryExists(file)) {
            return null;
        }

        List<KVObject> kvObjects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Deserialize line into KVObject
                KVObject kvObject = deserialize(line);
                kvObjects.add(kvObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kvObjects;
    }

    @Override
    public int countKVObjects(String identifier) {
        File file = getFileForIdentifier(identifier);

        if (!ensureFileAndDirectoryExists(file) || file.length() == 0) {
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

    private File getFileForIdentifier(String identifier) {
        return new File(storageDirectory, identifier + ".json"); // Store each identifier in a separate JSON file
    }

    private KVObject deserialize(String line) {
        JSONObject jsonObject = new JSONObject(line);
        String identifier = jsonObject.getString("identifier");
        JSONObject fieldTypeMapJSON = jsonObject.getJSONObject("fieldTypeMap");
        Map<String, KVObjectField> fieldTypeMap = new HashMap<>();

        // Assuming fieldTypeMapJSON is structured properly
        for (String key : fieldTypeMapJSON.keySet()) {
            KVObjectField field = new KVObjectField(
                fieldTypeMapJSON.getJSONObject(key).getString("field"),
                fieldTypeMapJSON.getJSONObject(key).getString("type"),
                fieldTypeMapJSON.getJSONObject(key).getBoolean("mandatory"),
                fieldTypeMapJSON.getJSONObject(key).getString("modifier"),
                fieldTypeMapJSON.getJSONObject(key).getString("defaultValue")
            );
            fieldTypeMap.put(key, field);
        }

        Map<String, String> jsonFields = new HashMap<>();
        for (String key : jsonObject.keySet()) {
            if (!key.equals("identifier") && !key.equals("fieldTypeMap")) {
                Object value = jsonObject.get(key);
                jsonFields.put(key, value != null ? value.toString() : null); // Convert to String
            }
        }
        return new KVObject(identifier, fieldTypeMap, jsonFields);
    }

    @Override
    public void close() {
    }
}