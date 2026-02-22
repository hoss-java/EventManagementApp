package com.EventManApp.storages;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.EventManApp.KVObject;
import com.EventManApp.KVSubject;
import com.EventManApp.KVObjectField;
import com.EventManApp.KVObjectStorage;
import com.EventManApp.lib.DebugUtil;
import com.EventManApp.lib.StringParserHelper;
import com.EventManApp.DatabaseConfig;
import com.EventManApp.storages.MongoDBKVSubjectStorage;

public class MongoDBKVObjectStorage implements KVObjectStorage {
    private static MongoDBKVObjectStorage instance;
    private DatabaseConfig dbConfigFile; 

    private MongoClient mongoClient;
    private MongoDatabase database;

    // Private constructor
    private MongoDBKVObjectStorage(DatabaseConfig dbConfigFile) {
        this.dbConfigFile = dbConfigFile; // Store the dbConfigFile in the instance

        String decryptedPassword = getDecryptedPassword(this.dbConfigFile);

        // Create credentials
        MongoCredential credential = MongoCredential.createCredential(dbConfigFile.getMongoUsername(), dbConfigFile.getMongoDatabase(), decryptedPassword.toCharArray());

        // Create a new MongoClient with corrected formatting
        String connectionString = String.format("mongodb://%s:%s@%s:%d/?authSource=%s",
            credential.getUserName(),
            new String(credential.getPassword()),
            dbConfigFile.getMongoAddress(),
            Integer.parseInt(dbConfigFile.getMongoPort()),
            dbConfigFile.getMongoDatabase() // Ensure the auth source is set to your db
        );

        // Create a new MongoClient
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase(dbConfigFile.getMongoDatabase());
    }

    public static synchronized MongoDBKVObjectStorage getInstance(DatabaseConfig dbConfigFile) {
        if (instance == null) {
            instance = new MongoDBKVObjectStorage(dbConfigFile);
        }
        return instance;
    }

    private String getDecryptedPassword(DatabaseConfig dbConfigFile) {
        String decryptedPassword = "";
        try {
            decryptedPassword = dbConfigFile.getMongoDecryptedPassword();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedPassword;
    }

    @Override
    public void addKVObject(KVObject kvObject) {
        String collectionName = kvObject.getIdentifier();
        MongoCollection<Document> collection = database.getCollection(collectionName);

        Document document = new Document();
        kvObject.getFieldTypeMap().forEach((fieldName, fieldType) -> {
            document.append(fieldName, kvObject.getFieldValue(fieldName).toString());
        });

        collection.insertOne(document);
        System.out.println("KVObject added to collection: " + collectionName);
    }

    @Override
    public boolean removeKVObject(KVObject kvObject) {
        String collectionName = kvObject.getIdentifier();
        MongoCollection<Document> collection = database.getCollection(collectionName);

        // Create a Document object to hold the query criteria, excluding the id
        Document query = new Document();
        
        kvObject.getFieldTypeMap().forEach((fieldName, fieldType) -> {
            if (!fieldName.equals("id")) { // Exclude the id field from the query
                query.append(fieldName, kvObject.getFieldValue(fieldName));
            }
        });

        long deletedCount = collection.deleteOne(query).getDeletedCount();
        System.out.println("Removed KVObject from collection: " + collectionName);
        return deletedCount > 0;
    }

    private boolean collectionExists(String collectionName) {
        boolean exists = false;

        // Get the list of existing collections in the database
        for (String name : this.database.listCollectionNames()) {
            if (name.equals(collectionName)) {
                exists = true; // Collection exists
                break;
            }
        }

        return exists; // Return whether the collection exists
    }

    @Override
    public List<KVObject> getKVObjects(String identifier) {
        // Get the instance of DatabaseKVSubjectStorage
        MongoDBKVSubjectStorage subjectStorage;

        try {
            subjectStorage = MongoDBKVSubjectStorage.getInstance(dbConfigFile);
        } catch (Exception e) {
            e.printStackTrace(); // or handle it in a way that makes sense for your application
            return null; // or you can throw a custom exception
        }
        
        // Check if the table with the given identifier exists
        if (!collectionExists(identifier)) {
            System.out.println("Error: No table found for identifier " + identifier + ". Cannot retrieve KVObjects.");
            return new ArrayList<>(); // Use ArrayList as the equivalent for empty collection
        }

        // Get the KVSubject associated with the identifier
        KVSubject kvSubject = subjectStorage.getKVSubject(identifier);
        if (kvSubject == null) {
            System.out.println("Error: No KVSubject found for identifier " + identifier + ". Cannot retrieve KVObjects.");
            return new ArrayList<>(); // Use ArrayList as the equivalent for empty collection
        }

        // Get the field type map from the KVSubject
        Map<String, KVObjectField> fieldTypeMap = kvSubject.getFieldTypeMap();
        if (fieldTypeMap == null || fieldTypeMap.isEmpty()) {
            System.out.println("Error: FieldTypeMap is empty for identifier " + identifier + ". Cannot retrieve KVObjects.");
            return new ArrayList<>(); // Use ArrayList as the equivalent for empty collection
        }

        // Prepare to read records from the specified table
        MongoCollection<Document> collection = database.getCollection(identifier);
        List<KVObject> kvObjects = new ArrayList<>();

        for (Document doc : collection.find()) {
            Map<String, String> jsonFields = new HashMap<>();
            for (String fieldName : doc.keySet()) {
                jsonFields.put(fieldName, doc.get(fieldName).toString());
            }
            KVObject kvObject = new KVObject(identifier, fieldTypeMap, jsonFields); // FieldTypeMap handling can be improved based on your requirements
            kvObjects.add(kvObject);
        }
        return kvObjects;
    }

    @Override
    public int countKVObjects(String identifier) {
        MongoCollection<Document> collection = database.getCollection(identifier);
        long count = collection.countDocuments();
        return (int) count;
    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDBKVObjectStorage connection closed.");
        }
    }
}
