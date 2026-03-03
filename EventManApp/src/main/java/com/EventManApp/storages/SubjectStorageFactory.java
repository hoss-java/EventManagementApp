package com.EventManApp.storages;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.EventManApp.kvhandler.KVSubjectStorage;
import com.EventManApp.storages.MongoDBKVSubjectStorage;
import com.EventManApp.storages.DatabaseKVSubjectStorage;
import com.EventManApp.storages.FileKVSubjectStorage;
import com.EventManApp.storages.MemoryKVSubjectStorage;

public class SubjectStorageFactory {
    public static KVSubjectStorage createKVSubjectStorage(String type, StorageSettings storageSettings) {
        switch (type.toLowerCase()) {
            case "memory":
                return new MemoryKVSubjectStorage(storageSettings);
            case "file":
                try {
                    return new FileKVSubjectStorage(storageSettings);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create FileKVSubjectStorage: " + e.getMessage(), e);
                }
            case "database":
                try {
                    return new DatabaseKVSubjectStorage(storageSettings); // Handle SQLException
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to create DatabaseKVSubjectStorage: " + e.getMessage(), e);
                }
            case "mongodb":
                try {
                    return new MongoDBKVSubjectStorage(storageSettings);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create MongoDBKVSubjectStorage: " + e.getMessage(), e);
                }
            default:
                throw new IllegalArgumentException("Unknown storage type: " + type);
        }
    }
}

