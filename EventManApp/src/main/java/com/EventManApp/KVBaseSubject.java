package com.EventManApp;

import com.EventManApp.ConfigManager;
import com.EventManApp.KVObjectField;
import com.EventManApp.lib.DebugUtil;

import java.util.HashMap;
import java.util.Map;

public abstract class KVBaseSubject {
    protected String identifier;
    protected Map<String, KVObjectField> fieldTypeMap;
    protected int nextId = 0; // Counter for generating unique IDs

    // Constructor
    public KVBaseSubject(String identifier) {
        this.identifier = identifier;
        // it needs to be initialized before using here
        ConfigManager configManager = ConfigManager.getInstance(null);
        this.nextId = configManager.getSetting(identifier,"nextid",this.nextId);
        this.fieldTypeMap = new HashMap<>(); // Initialize the map
    }

    // Getter for fieldTypeMap
    public Map<String, KVObjectField> getFieldTypeMap() {
        return fieldTypeMap;
    }

    // Setter for fieldTypeMap
    public void setFieldTypeMap(Map<String, KVObjectField> fieldTypeMap) {
        this.fieldTypeMap = fieldTypeMap; // Set the new fieldTypeMap
    }

    // Method to get the next ID
    public void setNextId(int nextId) {
        ConfigManager configManager = ConfigManager.getInstance(null);
        this.nextId = nextId == 0 ? nextId : this.nextId;
        configManager.setSetting(this.identifier,"nextid",this.nextId);
        
    }

    // Method to get the next ID
    public int getNextId() {
        this.nextId++;
        ConfigManager configManager = ConfigManager.getInstance(null);
        configManager.setSetting(this.identifier,"nextid",this.nextId);
        return this.nextId;
    }

    // Method to get the identifier
    public String getIdentifier() {
        return identifier;
    }
}
