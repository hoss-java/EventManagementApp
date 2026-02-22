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

import com.EventManApp.KVObjectField;
import com.EventManApp.KVSubject;
import com.EventManApp.KVSubjectStorage;
import com.EventManApp.DatabaseConfig;
import com.EventManApp.lib.DebugUtil;

public class MongoDBKVSubjectStorage implements KVSubjectStorage {
    private static MongoDBKVSubjectStorage instance;
    private DatabaseConfig dbConfigFile; // Add this field

    private MongoClient mongoClient;
    private MongoDatabase database;

    // Private constructor
    private MongoDBKVSubjectStorage(DatabaseConfig dbConfigFile) {
        this.dbConfigFile = dbConfigFile; // Store the dbConfigFile in the instance
        printMongoDBConnectionDetails();

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
        DebugUtil.debug(connectionString);
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase(dbConfigFile.getMongoDatabase());
    }

    public static synchronized MongoDBKVSubjectStorage getInstance(DatabaseConfig dbConfigFile) {
        if (instance == null) {
            instance = new MongoDBKVSubjectStorage(dbConfigFile);
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

    private void printMongoDBConnectionDetails() {
        // Print the connection details directly
        System.out.println("MongoDB Connection Details:");
        System.out.println("MongoDB Address: " + dbConfigFile.getMongoAddress());
        System.out.println("MongoDB Port: " + dbConfigFile.getMongoPort());
        System.out.println("Database Name: " + dbConfigFile.getMongoDatabase()); // e.g., "myDatabase"
        System.out.println("Username: " + dbConfigFile.getMongoUsername()); // e.g., "yourUsername"

        // Check if the MongoDB driver class can be loaded
        try {
            Class.forName("com.mongodb.client.MongoClient");
            System.out.println("MongoDB Java Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("MongoDB Java Driver not found.");
        }

        // Log the current thread's context class loader
        System.out.println("Current ClassLoader: " + Thread.currentThread().getContextClassLoader());
    }

    @Override
    public void addKVSubject(KVSubject kvSubject) {
        // Retrieve the database
        MongoCollection<Document> collection = database.getCollection("KVSubjects"); // Name of the collection

        Document doc = new Document("identifier", kvSubject.getIdentifier())
                .append("description", kvSubject.getDescription())
                .append("nextId", kvSubject.getNextId())
                .append("fieldTypeMap", serializeFieldTypeMap(kvSubject.getFieldTypeMap()));

        collection.insertOne(doc);
    }

    @Override
    public boolean removeKVSubject(KVSubject kvSubject) {
        // Retrieve the database
        MongoCollection<Document> collection = database.getCollection("KVSubjects"); // Name of the collection

        // Remove the document from the collection
        long deletedCount = collection.deleteOne(Filters.eq("identifier", kvSubject.getIdentifier())).getDeletedCount();
        return deletedCount > 0;
    }

    @Override
    public KVSubject getKVSubject(String identifier) {
        // Retrieve the database
        MongoCollection<Document> collection = database.getCollection("KVSubjects"); // Name of the collection

        Document doc = collection.find(Filters.eq("identifier", identifier)).first();
        if (doc != null) {
            KVSubject kvSubject = new KVSubject(identifier, doc.getString("description"));
            kvSubject.setNextId(doc.getInteger("nextId"));

            // Deserialize the fieldTypeMap from JSON
            kvSubject.setFieldTypeMap(deserializeFieldTypeMap(doc.getString("fieldTypeMap")));

            return kvSubject;
        }
        return null;
    }

    @Override
    public List<KVSubject> getAllKVSubjects() {
        List<KVSubject> subjects = new ArrayList<>();

        // Retrieve the database
        MongoCollection<Document> collection = database.getCollection("KVSubjects"); // Name of the collection

        for (Document doc : collection.find()) {
            String identifier = doc.getString("identifier");
            String description = doc.getString("description");
            int nextId = doc.getInteger("nextId");
            KVSubject kvSubject = new KVSubject(identifier, description);
            kvSubject.setNextId(nextId);

            // Deserialize fieldTypeMap
            kvSubject.setFieldTypeMap(deserializeFieldTypeMap(doc.getString("fieldTypeMap")));

            subjects.add(kvSubject);
        }
        return subjects;
    }

    @Override
    public int countKVSubjects() {
        // Retrieve the database
        MongoCollection<Document> collection = database.getCollection("KVSubjects"); // Name of the collection

        return (int) collection.countDocuments();
    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    // Serialize fieldTypeMap to JSON string
    private String serializeFieldTypeMap(Map<String, KVObjectField> fieldTypeMap) {
        Document jsonObject = new Document();
        for (KVObjectField field : fieldTypeMap.values()) {
            Document fieldObject = new Document("field", field.getField())
                    .append("type", field.getType())
                    .append("mandatory", field.isMandatory())
                    .append("modifier", field.getModifier())
                    .append("defaultValue", field.getDefaultValue());
            jsonObject.append(field.getField(), fieldObject);
        }
        return jsonObject.toJson();
    }

    // Deserialize fieldTypeMap from JSON string
    private Map<String, KVObjectField> deserializeFieldTypeMap(String jsonString) {
        Map<String, KVObjectField> fieldTypeMap = new HashMap<>();
        Document jsonObject = Document.parse(jsonString);
        for (String key : jsonObject.keySet()) {
            Document fieldObject = jsonObject.get(key, Document.class);
            KVObjectField field = new KVObjectField(
                    fieldObject.getString("field"),
                    fieldObject.getString("type"),
                    fieldObject.getBoolean("mandatory"),
                    fieldObject.getString("modifier"),
                    fieldObject.getString("defaultValue")
            );
            fieldTypeMap.put(field.getField(), field);
        }
        return fieldTypeMap;
    }
}
