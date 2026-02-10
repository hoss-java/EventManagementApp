package com.EventManApp;

import java.util.HashMap;
import java.util.Map;

import com.EventManApp.KVObjectField;

public abstract class KVBaseSubject {
    protected String identifier;
    protected Map<String, KVObjectField> fieldTypeMap;
    protected int nextId; // Counter for generating unique IDs

    // Constructor
    public KVBaseSubject(String identifier) {
        this.identifier = identifier;
        this.nextId = 1; // Start ID from 1
        this.fieldTypeMap = new HashMap<>(); // Initialize the map
    }

    // Getter for fieldTypeMap
    public Map<String, KVObjectField> getFieldTypeMap() {
        return fieldTypeMap;
    }

    // Method to get the next ID
    public int getNextId() {
        return nextId++;
    }

    // Method to get the identifier
    public String getIdentifier() {
        return identifier;
    }
}
