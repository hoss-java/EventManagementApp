package com.EventManApp;

public class KVObjectStorageFactory {
    public static KVObjectStorage createKVObjectStorage(String type, Object config) {
        switch (type.toLowerCase()) {
            case "memory":
                return new MemoryKVObjectStorage();
            case "file":
                return new FileKVObjectStorage((File) config);
            case "database":
                return new DatabaseKVObjectStorage((Connection) config);
            default:
                throw new IllegalArgumentException("Unknown storage type: " + type);
        }
    }
}
