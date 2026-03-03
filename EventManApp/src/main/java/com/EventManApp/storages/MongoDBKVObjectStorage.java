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

import com.EventManApp.kvhandler.KVObject;
import com.EventManApp.kvhandler.KVSubject;
import com.EventManApp.kvhandler.KVObjectField;
import com.EventManApp.kvhandler.KVObjectStorage;
import com.EventManApp.kvhandler.KVSubjectStorage;
import com.EventManApp.helper.DebugUtil;
import com.EventManApp.helper.StringParserHelper;
import com.EventManApp.helper.EncryptionUtil;
import com.EventManApp.config.StorageConfig;
import com.EventManApp.storages.MongoDBKVSubjectStorage;
import com.EventManApp.storages.StorageSettings;

public class MongoDBKVObjectStorage implements KVObjectStorage {
    private StorageSettings dbSettings; 

    private MongoClient mongoClient;
    private MongoDatabase database;

    public MongoDBKVObjectStorage(StorageSettings dbSettings) {
        this.dbSettings = dbSettings; // Store the dbSettings in the instance

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
    public void updateKVObject(KVObject kvObject) {
        String collectionName = kvObject.getIdentifier();
        MongoCollection<Document> collection = database.getCollection(collectionName);

        Document document = new Document();
        kvObject.getFieldTypeMap().forEach((fieldName, fieldType) -> {
            document.append(fieldName, kvObject.getFieldValue(fieldName).toString());
        });

        String id = document.getString("id");
        collection.replaceOne(Filters.eq("id", id), document);
        System.out.println("KVObject updated in collection: " + collectionName);
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
        // Get the MultiNamespaceStorageManager instance
        MultiNamespaceStorageManager manager = MultiNamespaceStorageManager.getInstance(null);
        
        // Get the namespace and storage type from dbSettings
        String namespace = dbSettings.getNamespace();
        String storageType = dbSettings.getStorageType();

        // Get the subject storage from the manager
        KVSubjectStorage subjectStorage;
        try {
            subjectStorage = manager.getSubjectStorage(namespace, storageType);
            
            if (!(subjectStorage instanceof MongoDBKVSubjectStorage)) {
                throw new IllegalArgumentException(
                    "Subject storage '" + storageType + "' in namespace '" + namespace + 
                    "' is not a MongoDBKVSubjectStorage instance"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        // Check if the collection with the given identifier exists
        if (!collectionExists(identifier)) {
            System.out.println("Error: No collection found for identifier " + identifier + 
                             ". Cannot retrieve KVObjects.");
            return new ArrayList<>();
        }

        // Get the KVSubject associated with the identifier
        KVSubject kvSubject = subjectStorage.getKVSubject(identifier);
        if (kvSubject == null) {
            System.out.println("Error: No KVSubject found for identifier " + identifier + 
                             ". Cannot retrieve KVObjects.");
            return new ArrayList<>();
        }

        String nameSpace = kvSubject.getNamespace();
        
        // Verify namespace consistency
        if (!nameSpace.equals(namespace)) {
            System.out.println("Warning: KVSubject namespace '" + nameSpace + 
                             "' does not match StorageSettings namespace '" + namespace + "'");
        }

        // Get the field type map from the KVSubject
        Map<String, KVObjectField> fieldTypeMap = kvSubject.getFieldTypeMap();
        if (fieldTypeMap == null || fieldTypeMap.isEmpty()) {
            System.out.println("Error: FieldTypeMap is empty for identifier " + identifier + 
                             ". Cannot retrieve KVObjects.");
            return new ArrayList<>();
        }

        // Prepare to read records from the specified collection
        MongoCollection<Document> collection = database.getCollection(identifier);
        List<KVObject> kvObjects = new ArrayList<>();

        for (Document doc : collection.find()) {
            Map<String, String> jsonFields = new HashMap<>();
            for (String fieldName : doc.keySet()) {
                jsonFields.put(fieldName, doc.get(fieldName).toString());
            }
            KVObject kvObject = new KVObject(nameSpace, identifier, fieldTypeMap, jsonFields);
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
