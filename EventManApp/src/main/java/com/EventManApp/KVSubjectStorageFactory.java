package com.EventManApp;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.EventManApp.KVSubjectStorage;
import com.EventManApp.storages.DatabaseKVSubjectStorage;
import com.EventManApp.storages.FileKVSubjectStorage;
import com.EventManApp.storages.MemoryKVSubjectStorage;
import com.EventManApp.DatabaseConfig;

public class KVSubjectStorageFactory {
    public static KVSubjectStorage createKVSubjectStorage(String type, Object config) {
        switch (type.toLowerCase()) {
            case "memory":
                return new MemoryKVSubjectStorage();
            case "file":
                if (config instanceof File) {
                    try {
                        return new FileKVSubjectStorage((File) config);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create FileKVSubjectStorage: " + e.getMessage(), e);
                    }
                } else {
                    throw new IllegalArgumentException("For 'file' storage, config must be of type File");
                }
            case "database":
                if (config instanceof DatabaseConfig) {
                    try {
                        return DatabaseKVSubjectStorage.getInstance((DatabaseConfig) config); // Handle SQLException
                    } catch (SQLException e) {
                        throw new RuntimeException("Failed to create DatabaseKVSubjectStorage: " + e.getMessage(), e);
                    }
                } else {
                    throw new IllegalArgumentException("For 'database' storage, config must be of type String");
                }
            default:
                throw new IllegalArgumentException("Unknown storage type: " + type);
        }
    }
}

