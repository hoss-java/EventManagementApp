package com.EventManApp;

public interface KVObjectStorage {
    void addKVObject(KVObject kvObject);
    boolean removeKVObject(KVObject kvObject);
    List<KVObject> getKVObject();
}