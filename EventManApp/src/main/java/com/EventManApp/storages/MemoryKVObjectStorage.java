package com.EventManApp.storages;

import java.util.ArrayList;
import java.util.List;

import com.EventManApp.KVObject;
import com.EventManApp.KVObjectStorage;
import com.EventManApp.lib.DebugUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryKVObjectStorage implements KVObjectStorage {
    private Map<String, List<KVObject>> kvObjectMap;

    public MemoryKVObjectStorage() {
        this.kvObjectMap = new HashMap<>();
    }

    @Override
    public void addKVObject(KVObject kvObject) {
        String identifier = kvObject.getIdentifier();
        kvObjectMap.computeIfAbsent(identifier, k -> new ArrayList<>()).add(kvObject);
    }

    @Override
    public boolean removeKVObject(KVObject kvObject) {
        String identifier = kvObject.getIdentifier();
        List<KVObject> objects = kvObjectMap.get(identifier);
        if (objects != null) {
            return objects.remove(kvObject); // Remove from the specific list
        }
        return false; // Return false if the list is not found
    }

    @Override
    public List<KVObject> getKVObjects(String identifier) {
        return new ArrayList<>(kvObjectMap.getOrDefault(identifier, new ArrayList<>())); // Return objects for the identifier
    }

    @Override
    public int countKVObjects(String identifier) {
        return kvObjectMap.getOrDefault(identifier, new ArrayList<>()).size(); // Count objects for the identifier
    }

    @Override
    public void close() {
    }
}

