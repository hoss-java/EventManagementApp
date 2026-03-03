package com.EventManApp.kvhandler;

import java.util.List;

import com.EventManApp.kvhandler.KVObject;
import com.EventManApp.helper.DebugUtil;

public interface KVObjectStorage {
    void addKVObject(KVObject kvObject);
    void updateKVObject(KVObject kvObject);
    boolean removeKVObject(KVObject kvObject);
    List<KVObject> getKVObjects(String identifier);
    int countKVObjects(String identifier);
    void close();
}