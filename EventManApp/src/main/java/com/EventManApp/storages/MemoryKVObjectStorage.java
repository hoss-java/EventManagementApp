package com.EventManApp.storages;

import java.util.ArrayList;
import java.util.List;

import com.EventManApp.KVObject;
import com.EventManApp.KVObjectStorage;
import com.EventManApp.lib.DebugUtil;

public class MemoryKVObjectStorage implements KVObjectStorage {
    private List<KVObject> kvObjects;

    public MemoryKVObjectStorage() {
        this.kvObjects = new ArrayList<>();
    }

    @Override
    public void addKVObject(KVObject kvObject) {
        kvObjects.add(kvObject);
    }

    @Override
    public boolean removeKVObject(KVObject kvObject) {
        return kvObjects.remove(kvObject);
    }

    @Override
    public List<KVObject> getKVObjects() {
        return new ArrayList<>(kvObjects);
    }

    @Override
    public int countKVObjects() {
        return kvObjects.size();
    }
}
