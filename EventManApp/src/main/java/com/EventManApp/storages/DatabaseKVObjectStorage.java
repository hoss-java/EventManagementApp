package com.EventManApp.storages;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.EventManApp.KVObject;
import com.EventManApp.KVObjectField;
import com.EventManApp.KVObjectStorage;
import com.EventManApp.lib.DebugUtil;

public class DatabaseKVObjectStorage implements KVObjectStorage {
    private Connection connection;

    public DatabaseKVObjectStorage(String databaseUrl) throws SQLException {
        // Establish a connection to the database
        this.connection = DriverManager.getConnection(databaseUrl);
        // Create the table for KVObjects if it doesn't exist
        String createTableSQL = "CREATE TABLE IF NOT EXISTS KVObjects (" +
                "identifier VARCHAR(255) PRIMARY KEY, " +
                "fieldTypeMap TEXT, " +
                "jsonFields TEXT)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    @Override
    public void addKVObject(KVObject kvObject) {
        String insertSQL = "INSERT INTO KVObjects (identifier, fieldTypeMap, jsonFields) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, kvObject.getIdentifier());
            //pstmt.setString(2, kvObject.getFieldTypeMapAsJSON()); // Assuming you have this method to convert fieldTypeMap to JSON string
            //pstmt.setString(3, kvObject.getJsonFieldsAsJSON()); // Assuming you have this method to convert jsonFields to JSON string
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean removeKVObject(KVObject kvObject) {
        String deleteSQL = "DELETE FROM KVObjects WHERE identifier = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, kvObject.getIdentifier());
            return pstmt.executeUpdate() > 0; // Return true if a row was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of exception
        }
    }

    @Override
    public List<KVObject> getKVObjects() {
        List<KVObject> kvObjects = new ArrayList<>();
        String selectAllSQL = "SELECT * FROM KVObjects";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectAllSQL)) {
            while (rs.next()) {
                String identifier = rs.getString("identifier");
                String fieldTypeMapString = rs.getString("fieldTypeMap");
                String jsonFieldsString = rs.getString("jsonFields");

                // Deserialize the fieldTypeMap and jsonFields strings back to their respective structures
                Map<String, KVObjectField> fieldTypeMap = deserializeFieldTypeMap(fieldTypeMapString);
                Map<String, String> jsonFields = deserializeJsonFields(jsonFieldsString);

                KVObject kvObject = new KVObject(identifier, fieldTypeMap, jsonFields);
                kvObjects.add(kvObject); // Add to list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kvObjects; // Return list of KVObjects
    }

    private Map<String, KVObjectField> deserializeFieldTypeMap(String fieldTypeMapString) {
        // Parse the fieldTypeMap JSON string to create a Map of KVObjectField
        JSONObject jsonObject = new JSONObject(fieldTypeMapString);
        Map<String, KVObjectField> fieldTypeMap = new HashMap<>();

        for (String key : jsonObject.keySet()) {
            JSONObject fieldJson = jsonObject.getJSONObject(key);
            KVObjectField field = new KVObjectField(
                fieldJson.getString("field"),
                fieldJson.getString("type"),
                fieldJson.getBoolean("mandatory"),
                fieldJson.getString("modifier"),
                fieldJson.getString("defaultValue")
            );
            fieldTypeMap.put(key, field);
        }

        return fieldTypeMap;
    }

    private Map<String, String> deserializeJsonFields(String jsonFieldsString) {
        // Parse the JSON string to create a Map of jsonFields
        Map<String, String> jsonFields = new HashMap<>();
        JSONObject jsonObject = new JSONObject(jsonFieldsString);

        for (String key : jsonObject.keySet()) {
            jsonFields.put(key, jsonObject.getString(key));
        }
        return jsonFields;
    }

    @Override
    public int countKVObjects() {
        String countSQL = "SELECT COUNT(*) AS total FROM KVObjects";
        try (PreparedStatement pstmt = connection.prepareStatement(countSQL);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total"); // Return the count of records
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions
        }
        return 0; // Return 0 if count fails or no records found
    }

    // Close the database connection when done
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions
        }
    }
}

