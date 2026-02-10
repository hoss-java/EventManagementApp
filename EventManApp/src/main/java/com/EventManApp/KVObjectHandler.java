package com.EventManApp;

import java.util.Arrays;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.EventManApp.KVObjectHandlerInterface;
import com.EventManApp.lib.ResponseHelper;
import com.EventManApp.lib.JSONHelper;
import com.EventManApp.ActionCallbackInterface;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.EventManApp.lib.DebugUtil;

public class KVObjectHandler implements KVObjectHandlerInterface {
    private String DEFAULT_RESPONSE_VERSION = "1.0"; // Default version
    private List<KVObject> kvObjects;
    private List<String> uniqueEntryCheckSkippedfields = Arrays.asList("id");
    private int nextId = 0;

    public KVObjectHandler(ActionCallbackInterface callback) {
        this.kvObjects = new ArrayList<>();
    }

    // Validator to check for duplicates in the existing collection
    private boolean isUniqueEntry(KVObject newKVObject, List<String> fieldsToSkip) {
        for (KVObject obj : this.kvObjects) {
            if ( (newKVObject.getIdentifier()).equals(obj.getIdentifier()) ) {
                boolean isMatch = true;
                for (String key : newKVObject.getFieldNames()) { // Assuming a method exists to get field names
                    // Skip comparison of the "id" field
                    if (fieldsToSkip.contains(key.toLowerCase())) {
                        continue;
                    }
                    Object valueToCompare = obj.getFieldValue(key); // Assuming a method exists to get field values
                    Object currentValue = newKVObject.getFieldValue(key);
                    if (currentValue != null && !currentValue.equals(valueToCompare)) {
                        isMatch = false;
                        break;
                    }
                }
                // If a match is found, return false
                if (isMatch) {
                    return false; // Duplicate found
                }
            }
        }
        return true; // No duplicates found
    }

    // Add a kvObject
    public Boolean addKVObject(KVObject kvObject) {
        // Validate before adding
        if (isUniqueEntry(kvObject,uniqueEntryCheckSkippedfields)) {
            kvObjects.add(kvObject);
            return true;
        } else {
            throw new IllegalArgumentException("A KVObject with the same non-id fields already exists.");
        }
    }

    // Remove a kvObject(s)
    public Boolean removeKVObject(String identifier,  Predicate<KVObject> validator) {
        if (kvObjects.removeIf(kvObject -> kvObject.getIdentifier().equals(identifier) && validator.test(kvObject))) {
            return true;
        }
        return false;
    }

    // Existing method that returns a KVObject based on identifier and a validator function
    public KVObject getKVObject(String identifier, Predicate<KVObject> validator) {
        return kvObjects.stream()
            .filter(kvObject -> kvObject.getIdentifier().equals(identifier) && validator.test(kvObject))
            .findFirst()
            .orElse(null); // Return null if not found
    }

    // Existing method that returns a list of KVObjects based on identifier and a validator function
    public List<KVObject> getKVObjects(String identifier, Predicate<KVObject> validator) {
        return kvObjects.stream()
            .filter(kvObject -> kvObject.getIdentifier().equals(identifier) && validator.test(kvObject))
            .collect(Collectors.toList()); // Collect matching KVObjects into a list
    }

    // New method to get a KVObject by identifier without a validator (default to accepting all)
    public KVObject getKVObject(String identifier) {
        return getKVObject(identifier, kvObject -> true); // Default validator that accepts all
    }

    // New method to get a list of KVObjects by identifier without a validator 
    public List<KVObject> getKVObjects(String identifier) {
        return getKVObjects(identifier, kvObject -> true); // Default validator that accepts all
    }
}