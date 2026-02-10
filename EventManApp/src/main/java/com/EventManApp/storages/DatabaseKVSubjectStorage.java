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

import org.json.JSONArray;
import org.json.JSONObject;

import com.EventManApp.KVSubject;
import com.EventManApp.KVSubjectStorage;
import com.EventManApp.lib.DebugUtil;

public class DatabaseKVSubjectStorage implements KVSubjectStorage {
    private Connection connection;

    public DatabaseKVSubjectStorage(String databaseUrl) throws SQLException {
        // Establish a connection to the database
        this.connection = DriverManager.getConnection(databaseUrl);
        // Create the table for KVSubjects if it doesn't exist
        String createTableSQL = "CREATE TABLE IF NOT EXISTS KVSubjects (" +
                "identifier VARCHAR(255) PRIMARY KEY, " +
                "description TEXT, " +
                "nextId INT)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    @Override
    public void addKVSubject(KVSubject kvSubject) {
        String insertSQL = "INSERT INTO KVSubjects (identifier, description, nextId) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, kvSubject.getIdentifier());
            pstmt.setString(2, kvSubject.getDescription());
            pstmt.setInt(3, kvSubject.getNextId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions
        }
    }

    @Override
    public boolean removeKVSubject(KVSubject kvSubject) {
        String deleteSQL = "DELETE FROM KVSubjects WHERE identifier = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, kvSubject.getIdentifier());
            return pstmt.executeUpdate() > 0; // Return true if a row was deleted
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions
            return false; // Return false in case of exception
        }
    }

    @Override
    public KVSubject getKVSubject(String identifier) {
        String selectSQL = "SELECT description, nextId FROM KVSubjects WHERE identifier = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, identifier);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String description = rs.getString("description");
                int nextId = rs.getInt("nextId");
                KVSubject kvSubject = new KVSubject(identifier, description);
                kvSubject.setNextId(nextId); // Set nextId
                return kvSubject;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions
        }
        return null; // Not found
    }

    @Override
    public List<KVSubject> getAllKVSubjects() {
        List<KVSubject> subjects = new ArrayList<>();
        String selectAllSQL = "SELECT identifier, description, nextId FROM KVSubjects";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectAllSQL)) {
            while (rs.next()) {
                String identifier = rs.getString("identifier");
                String description = rs.getString("description");
                int nextId = rs.getInt("nextId");
                KVSubject kvSubject = new KVSubject(identifier, description);
                kvSubject.setNextId(nextId); // Set nextId
                subjects.add(kvSubject);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions
        }
        return subjects; // Return list of KVSubjects
    }

    @Override
    public int countKVSubjects() {
        String countSQL = "SELECT COUNT(*) AS total FROM KVSubjects";
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
