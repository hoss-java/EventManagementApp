package com.EventManApp;

import org.json.JSONArray;
import org.json.JSONObject;

import com.EventManApp.KVBaseSubject;
import com.EventManApp.KVObjectField;

public class KVSubject extends KVBaseSubject {
    private String description; // New field for additional string data

    // Constructor
    public KVSubject(String identifier, String description) {
        super(identifier);
        this.description = description; // Initialize the new field
    }

    // Constructor with only identifier
    public KVSubject(String identifier) {
        super(identifier);
        this.description = ""; // Initialize description to an empty string by default
    }

    // Getter for description
    public String getDescription() {
        return description;
    }

    // Setter for description
    public void setDescription(String description) {
        this.description = description; // Allow modification of the description
    }

    // Method to convert KVSubject to JSONObject
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("identifier", this.identifier);
        jsonObject.put("description", this.description);

        // Convert fieldTypeMap to JSON array
        JSONArray fieldsJsonArray = new JSONArray();
        for (KVObjectField field : fieldTypeMap.values()) {
            fieldsJsonArray.put(field.toJSON());
        }
        jsonObject.put("fieldTypeMap", fieldsJsonArray);
        
        jsonObject.put("nextId", this.nextId);

        return jsonObject;  // Return the JSON object
    }

    @Override
    public String toString() {
        return toJSON().toString(); // Use toJSON for the string representation
    }
}