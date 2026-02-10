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
import com.EventManApp.KVObjectStorage;
import com.EventManApp.lib.ResponseHelper;
import com.EventManApp.lib.JSONHelper;
import com.EventManApp.ActionCallbackInterface;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.EventManApp.lib.DebugUtil;

public class KVObjectHandler implements KVObjectHandlerInterface {
    private String DEFAULT_RESPONSE_VERSION = "1.0"; // Default version
    //private List<KVObject> kvObjects;
    private KVObjectStorage kvObjectStorage; // Reference to KVObjectStorage
    private List<String> uniqueEntryCheckSkippedfields = Arrays.asList("id");
    private int nextId = 0;

    public KVObjectHandler(ActionCallbackInterface callback, KVObjectStorage storage) {
        //this.kvObjects = new ArrayList<>();
        this.kvObjectStorage = storage; // Initialize using injected storage
    }

    // Validator to check for duplicates in the existing collection
    private boolean isUniqueEntry(KVObject newKVObject, List<String> fieldsToSkip) {
        // Use a validator to match the identifier and check fields, skipping those specified
        Predicate<KVObject> validator = existingObject -> {
            // Check for matching identifier
            if (!existingObject.getIdentifier().equals(newKVObject.getIdentifier())) {
                return false; // Different identifiers are not a match
            }

            // Check other fields, skipping specified ones
            for (String key : newKVObject.getFieldNames()) {
                if (fieldsToSkip.contains(key.toLowerCase())) {
                    continue; // Skip the fields specified for skipping
                }
                Object valueToCompare = existingObject.getFieldValue(key);
                Object currentValue = newKVObject.getFieldValue(key);
                if (currentValue != null && !currentValue.equals(valueToCompare)) {
                    return false; // Mismatch found
                }
            }
            return true; // All fields match
        };

        // Check if a matching object exists using the getKVObject method
        KVObject existingMatch = getKVObject(newKVObject.getIdentifier(), validator);
        return existingMatch == null; // If no match, it's unique
    }

    // Add a kvObject
    public Boolean addKVObject(KVObject kvObject) {
        // Validate before adding
        if (isUniqueEntry(kvObject,uniqueEntryCheckSkippedfields)) {
            kvObjectStorage.addKVObject(kvObject);
            return true;
        } else {
            throw new IllegalArgumentException("A KVObject with the same non-id fields already exists.");
        }
    }

    // Remove a kvObject(s)
    public Boolean removeKVObject(String identifier,  Predicate<KVObject> validator) {
        KVObject kvObjectToRemove = getKVObject(identifier, validator);
        if (kvObjectToRemove != null) {
            return kvObjectStorage.removeKVObject(kvObjectToRemove); // Use storage's remove method
        }
        return false;
    }

    // Existing method that returns a KVObject based on identifier and a validator function
    public KVObject getKVObject(String identifier, Predicate<KVObject> validator) {
        return kvObjectStorage.getKVObjects().stream()
            .filter(kvObject -> kvObject.getIdentifier().equals(identifier) && validator.test(kvObject))
            .findFirst()
            .orElse(null);
    }

    // Existing method that returns a list of KVObjects based on identifier and a validator function
    public List<KVObject> getKVObjects(String identifier, Predicate<KVObject> validator) {
        return kvObjectStorage.getKVObjects().stream()
            .filter(kvObject -> kvObject.getIdentifier().equals(identifier) && validator.test(kvObject))
            .collect(Collectors.toList());
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