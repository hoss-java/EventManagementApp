package com.EventManApp.storages;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.EventManApp.KVObjectStorage;
import com.EventManApp.KVObject;
import com.EventManApp.KVObjectField;
import com.EventManApp.lib.DebugUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileKVObjectStorage implements KVObjectStorage {
    private File file;

    public FileKVObjectStorage(File file) throws IOException {
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
    public void addKVObject(KVObject kvObject) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(kvObject.toString()); // Serialize the KVObject as a JSON string
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean removeKVObject(KVObject kvObject) {
        List<KVObject> kvObjects = getKVObjects();
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
    public List<KVObject> getKVObjects() {
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

    private KVObject deserialize(String line) {
        // Here we assume the line is in JSON format. Implement a method to convert JSON to KVObject.
        // For simplicity, we use a dummy implementation. Replace with actual deserialization.
        // You will need a proper constructor or factory method for creating KVObject from JSON.
        
        // Example JSON parsing (this will require actual JSON parsing logic)
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
                jsonFields.put(key, jsonObject.getString(key));
            }
        }

        return new KVObject(identifier, fieldTypeMap, jsonFields);
    }
}

