package com.EventManApp.storages;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.EventManApp.KVObjectStorage;
import com.EventManApp.KVObject;
import com.EventManApp.lib.DebugUtil;

public class DatabaseKVObjectStorage implements KVObjectStorage {
    private Connection connection;

    public DatabaseKVObjectStorage(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addKVObject(KVObject kvObject) {
        String sql = "INSERT INTO kvObject (data) VALUES (?)"; // Customize as needed
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kvObject.toString()); // Customize serialization
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean removeKVObject(KVObject kvObject) {
        // Implement logic to remove an kvObject from the database
        throw new UnsupportedOperationException("Database removal not implemented yet.");
    }

    @Override
    public List<KVObject> getKVObjects() {
        List<KVObject> kvObjects = new ArrayList<>();
        String sql = "SELECT * FROM kvObjects"; // Customize as needed
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                //KVObject kvObject = new KVObject(rs.getString("data")); // Customize deserialization
                //kvObjects.add(kvObject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kvObjects;
    }
}
