package com.EventManApp.storages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.EventManApp.KVSubject;
import com.EventManApp.KVSubjectStorage;
import com.EventManApp.lib.DebugUtil;

public class MemoryKVSubjectStorage implements KVSubjectStorage {
    private Map<String, KVSubject> kvSubjects;

    public MemoryKVSubjectStorage() {
        this.kvSubjects =  new HashMap<>();
    }

    @Override
    public void addKVSubject(KVSubject kvSubject) {
        kvSubjects.put(kvSubject.getIdentifier(), kvSubject); // Store with identifier as key
    }

    @Override
    public boolean removeKVSubject(KVSubject kvSubject) {
        return kvSubjects.remove(kvSubject.getIdentifier()) != null; // Use identifier for removal
    }

    @Override
    public KVSubject getKVSubject(String identifier) {
        return kvSubjects.get(identifier); // Get by identifier
    }

    @Override
    public List<KVSubject> getAllKVSubjects() {
        return new ArrayList<>(kvSubjects.values()); // Return all values
    }

    @Override
    public int countKVSubjects() {
        return kvSubjects.size(); // Return the number of subjects stored in memory
    }
}
