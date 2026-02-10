package com.EventManApp;

import java.util.List;

import com.EventManApp.KVObject;
import com.EventManApp.lib.DebugUtil;

public interface KVObjectStorage {
    void addKVObject(KVObject kvObject);
    boolean removeKVObject(KVObject kvObject);
    List<KVObject> getKVObjects();
    int countKVObjects();
}