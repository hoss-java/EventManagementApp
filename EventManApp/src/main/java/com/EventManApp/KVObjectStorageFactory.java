package com.EventManApp;

import java.io.*;
import java.sql.*;

import com.EventManApp.KVObjectStorage;
import com.EventManApp.storages.DatabaseKVObjectStorage;
import com.EventManApp.storages.FileKVObjectStorage;
import com.EventManApp.storages.MemoryKVObjectStorage;

public class KVObjectStorageFactory {
    public static KVObjectStorage createKVObjectStorage(String type, Object config) {
        switch (type.toLowerCase()) {
            case "memory":
                return new MemoryKVObjectStorage();
            case "file":
                if (config instanceof File) {
                    try {
                        return new FileKVObjectStorage((File) config);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create FileKVObjectStorage: " + e.getMessage(), e);
                    }
                } else {
                    throw new IllegalArgumentException("For 'file' storage, config must be of type File");
                }
            case "database":
                if (config instanceof Connection) {
                    return new DatabaseKVObjectStorage((Connection) config);
                } else {
                    throw new IllegalArgumentException("For 'database' storage, config must be of type Connection");
                }
            default:
                throw new IllegalArgumentException("Unknown storage type: " + type);
        }
    }
}
