package com.EventManApp.storages;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.EventManApp.kvhandler.KVObjectField;
import com.EventManApp.kvhandler.KVSubjectAttribute;
import com.EventManApp.kvhandler.KVSubject;
import com.EventManApp.kvhandler.KVSubjectStorage;
import com.EventManApp.kvhandler.SerializationUtil;
import com.EventManApp.config.StorageConfig;
import com.EventManApp.helper.DebugUtil;
import com.EventManApp.helper.EncryptionUtil;
import com.EventManApp.storages.StorageSettings;

public class MongoDBKVSubjectStorage implements KVSubjectStorage {
    private StorageSettings dbSettings;

    private MongoClient mongoClient;
    private MongoDatabase database;

    public MongoDBKVSubjectStorage(StorageSettings dbSettings) {
        this.dbSettings = dbSettings; // Store the dbSettings in the instance
        printMongoDBConnectionDetails();

        String decryptedPassword = getDecryptedPassword(dbSettings);

        // Create credentials
        MongoCredential credential = MongoCredential.createCredential(dbSettings.get("username"), dbSettings.get("database"), decryptedPassword.toCharArray());

        // Create a new MongoClient with corrected formatting
        String connectionString = String.format("mongodb://%s:%s@%s:%d/?authSource=%s",
            credential.getUserName(),
            new String(credential.getPassword()),
            dbSettings.get("address"),
            Integer.parseInt(dbSettings.get("port")),
            dbSettings.get("database") // Ensure the auth source is set to your db
        );

        // Create a new MongoClient
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase(dbSettings.get("database"));
    }

    private String getDecryptedPassword(StorageSettings dbSettings) {
        String decryptedPassword = "";
        try {
            decryptedPassword = createDecryptedPassword();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedPassword;
    }

    private SecretKey stringToKey(String keyStr) {
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    // Decrypt the stored encrypted password
    public String createDecryptedPassword() throws Exception {
        String keyString = dbSettings.get("secretKey");
        SecretKey dbSecretKey = stringToKey(keyString);
        String dbEncryptedPassword = dbSettings.get("password");
        if (dbEncryptedPassword == null || dbSecretKey == null) {

            return null;
        }
        return EncryptionUtil.decrypt(dbEncryptedPassword, dbSecretKey);
    }

    private void printMongoDBConnectionDetails() {
        // Print the connection details directly
        System.out.println("MongoDB Connection Details:");
        System.out.println(" MongoDB Address: " + dbSettings.get("address"));
        System.out.println(" MongoDB Port: " + dbSettings.get("port"));
        System.out.println(" Database Name: " + dbSettings.get("database")); // e.g., "myDatabase"
        System.out.println(" Username: " + dbSettings.get("username")); // e.g., "yourUsername"

        // Check if the MongoDB driver class can be loaded
        try {
            Class.forName("com.mongodb.client.MongoClient");
            System.out.println("MongoDB Java Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("MongoDB Java Driver not found.");
        }

        // Log the current thread's context class loader
        //System.out.println("Current ClassLoader: " + Thread.currentThread().getContextClassLoader());
    }

    @Override
    public void addKVSubject(KVSubject kvSubject) {
        MongoCollection<Document> collection = database.getCollection("KVSubjects");

        Document doc = new Document("identifier", kvSubject.getIdentifier())
                .append("attribute", SerializationUtil.serialize(kvSubject.getAttribute()))
                .append("fieldTypeMap", SerializationUtil.serializeMap(kvSubject.getFieldTypeMap()));

        collection.insertOne(doc);
    }

    @Override
    public void updateKVSubject(KVSubject kvSubject) {
        MongoCollection<Document> collection = database.getCollection("KVSubjects");

        Document doc = new Document("attribute", SerializationUtil.serialize(kvSubject.getAttribute()))
                .append("fieldTypeMap", SerializationUtil.serializeMap(kvSubject.getFieldTypeMap()));

        collection.updateOne(Filters.eq("identifier", kvSubject.getIdentifier()), new Document("$set", doc));
    }

    @Override
    public boolean removeKVSubject(KVSubject kvSubject) {
        // Retrieve the database
        MongoCollection<Document> collection = database.getCollection("KVSubjects");

        // Remove the document from the collection using the identifier from attributes
        long deletedCount = collection.deleteOne(Filters.eq("identifier", kvSubject.getIdentifier())).getDeletedCount();
        return deletedCount > 0;
    }

    @Override
    public KVSubject getKVSubject(String identifier) {
        // Retrieve the database
        MongoCollection<Document> collection = database.getCollection("KVSubjects");

        Document doc = collection.find(Filters.eq("identifier", identifier)).first();
        if (doc != null) {
            // Deserialize the subjectAttribute
            KVSubjectAttribute subjectAttribute = SerializationUtil.deserialize(doc.getString("subjectAttribute"), KVSubjectAttribute.class);
            
            KVSubject kvSubject = new KVSubject(subjectAttribute);

            // Deserialize the fieldTypeMap from JSON
            kvSubject.setFieldTypeMap(SerializationUtil.deserializeMap(doc.getString("fieldTypeMap"), KVObjectField.class));

            return kvSubject;
        }
        return null;
    }

    @Override
    public List<KVSubject> getAllKVSubjects() {
        List<KVSubject> subjects = new ArrayList<>();

        // Retrieve the database
        MongoCollection<Document> collection = database.getCollection("KVSubjects");

        for (Document doc : collection.find()) {
            // Deserialize the subjectAttribute
            KVSubjectAttribute subjectAttribute = SerializationUtil.deserialize(doc.getString("subjectAttribute"), KVSubjectAttribute.class);
            
            KVSubject kvSubject = new KVSubject(subjectAttribute);

            // Deserialize fieldTypeMap
            kvSubject.setFieldTypeMap(SerializationUtil.deserializeMap(doc.getString("fieldTypeMap"), KVObjectField.class));

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
            System.out.println("MongoDBKVSubjectStorage connection closed.");
        }
    }

}
