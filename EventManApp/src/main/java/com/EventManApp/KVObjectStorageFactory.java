package com.EventManApp;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.EventManApp.KVObjectStorage;
import com.EventManApp.storages.DatabaseKVObjectStorage;
import com.EventManApp.storages.FileKVObjectStorage;
import com.EventManApp.storages.MemoryKVObjectStorage;
import com.EventManApp.DatabaseConfig;

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
                if (config instanceof DatabaseConfig) {
                    try {
                        return DatabaseKVObjectStorage.getInstance((DatabaseConfig) config); // Handle SQLException
                    } catch (SQLException e) {
                        throw new RuntimeException("Failed to create DatabaseKVObjectStorage: " + e.getMessage(), e);
                    }
                } else {
                    throw new IllegalArgumentException("For 'database' storage, config must be of type String");
                }
            default:
                throw new IllegalArgumentException("Unknown storage type: " + type);
        }
    }
}
