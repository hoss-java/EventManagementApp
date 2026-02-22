package com.EventManApp.storages;

import java.sql.Driver;
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
import java.util.Enumeration;
import java.util.function.Predicate;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.EventManApp.KVObjectField;
import com.EventManApp.KVSubject;
import com.EventManApp.KVSubjectStorage;
import com.EventManApp.lib.DebugUtil;
import com.EventManApp.DatabaseConfig;

public class DatabaseKVSubjectStorage implements KVSubjectStorage {
    private static DatabaseKVSubjectStorage instance;
    private DatabaseConfig dbConfigFile; // Add this field
    // JDBC connection
    private Connection connection;

    // Private constructor
    private DatabaseKVSubjectStorage(DatabaseConfig dbConfigFile) throws SQLException {
        this.dbConfigFile = dbConfigFile; // Store the dbConfigFile in the instance
        printDefaultConnectionDetails();

        String decryptedPassword = getDecryptedPassword(dbConfigFile);

        // Establish a connection to the database
        this.connection = DriverManager.getConnection(dbConfigFile.getSqlUrl(), dbConfigFile.getSqlUsername(), decryptedPassword);

        // Create the table for KVSubjects if it doesn't exist, including fieldTypeMap
        String createTableSQL = "CREATE TABLE IF NOT EXISTS KVSubjects (" +
                "identifier VARCHAR(255) PRIMARY KEY, " +
                "description TEXT, " +
                "nextId INT, " +
                "fieldTypeMap TEXT)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public static synchronized DatabaseKVSubjectStorage getInstance(DatabaseConfig dbConfigFile) throws SQLException {
        if (instance == null) {
            instance = new DatabaseKVSubjectStorage(dbConfigFile);
        }
        return instance;
    }

    private String getDecryptedPassword(DatabaseConfig dbConfigFile) {
        String decryptedPassword = "";
        try {
            decryptedPassword = dbConfigFile.getSqlDecryptedPassword();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedPassword;
    }

    private void printDefaultConnectionDetails() {
        String jdbcUrl = dbConfigFile.getSqlUrl();
        String username = dbConfigFile.getSqlUsername();

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            System.out.println("Registered driver: " + driver.getClass().getName());
        }

        // Print the connection details
        System.out.println("Attempting to connect to the database with the following settings:");
        System.out.println("JDBC URL: " + jdbcUrl);
        System.out.println("Username: " + username);
        
        try {
            // Try to load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
        }

        // You can also log the current thread's context class loader
        System.out.println("Current ClassLoader: " + Thread.currentThread().getContextClassLoader());
        DriverManager.setLogWriter(new PrintWriter(System.out));
    }

    // Method to determine the database type based on the connection
    private String getDatabaseType() {
        String url = "";
        try {
            if (connection != null) {
                url = connection.getMetaData().getURL();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately
        }

        if (url.contains("sqlserver")) {
            return "SQLSERVER";
        } else if (url.contains("mysql")) {
            return "SQLSERVER";
        } else if (url.contains("sqlite")) {
            return "SQLITE";
        } else {
            return "UNKNOWN"; // Handle unsupported types appropriately
        }
    }

    private boolean tableExists(String tableName) {
        boolean exists = false; // Variable to store if the table exists
        String dbType = getDatabaseType(); // Implement this method to return the DB type
        String checkTableSQL;

        try {
            if ("SQLSERVER".equalsIgnoreCase(dbType)) {
                checkTableSQL = "SELECT COUNT(*) FROM information_schema.tables "
                              + "WHERE table_schema = ? AND table_name = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(checkTableSQL)) {
                    pstmt.setString(1, dbConfigFile.getSqlDatabase());
                    pstmt.setString(2, tableName);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        exists = rs.getInt(1) > 0; // If count is greater than 0, the table exists
                    }
                }
            } else if ("SQLITE".equalsIgnoreCase(dbType)) {
                checkTableSQL = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?";
                try (PreparedStatement pstmt = connection.prepareStatement(checkTableSQL)) {
                    pstmt.setString(1, tableName);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        exists = rs.getInt(1) > 0; // If count is greater than 0, the table exists
                    }
                }
            } else {
                throw new UnsupportedOperationException("Unsupported database type: " + dbType);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }

        return exists; // Return whether the table exists
    }

    private void createTableFromFieldTypeMap(KVSubject kvSubject) {
        StringBuilder createTableSQL = new StringBuilder("CREATE TABLE " + kvSubject.getIdentifier() + " (");
        
        // Iterate over the fieldTypeMap to create columns
        for (KVObjectField field : kvSubject.getFieldTypeMap().values()) {
            createTableSQL.append(field.getField()).append(" ").append(field.getSqlType()); // Use new getSqlType()
            if (field.isMandatory()) {
                createTableSQL.append(" NOT NULL"); // Set mandatory constraint
            }
            createTableSQL.append(", "); // Commas between columns
        }
        
        // Remove the last comma and space
        createTableSQL.setLength(createTableSQL.length() - 2);
        createTableSQL.append(")");

        // Execute the create table SQL
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL.toString());
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions
        }
    }

    @Override 
    public void addKVSubject(KVSubject kvSubject) {
        // Check if table with the same name exists
        if (tableExists(kvSubject.getIdentifier())) {
            System.out.println("Error: A table with the name " + kvSubject.getIdentifier() + " already exists. Please delete the table to proceed.");
            return; // Table exists, do not add the subject
        }

        // Insert new KVSubject and create a corresponding table
        String insertSQL = "INSERT INTO KVSubjects (identifier, description, nextId, fieldTypeMap) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, kvSubject.getIdentifier());
            pstmt.setString(2, kvSubject.getDescription());
            pstmt.setInt(3, kvSubject.getNextId());

            // Serialize fieldTypeMap to JSON
            String fieldTypeMapJson = serializeFieldTypeMap(kvSubject.getFieldTypeMap());
            pstmt.setString(4, fieldTypeMapJson); // Set the serialized fieldTypeMap
            pstmt.executeUpdate();
            
            // Create a table with the name of the identifier based on fieldTypeMap
            createTableFromFieldTypeMap(kvSubject);
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions
        }
    }

    private String getCurrentDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return LocalDateTime.now().format(formatter); // Format date and time
    }

    @Override
    public boolean removeKVSubject(KVSubject kvSubject) {
        // Check if the table with the same name exists
        if (tableExists(kvSubject.getIdentifier())) {
            // Prepare the new table name with date and time
            String newTableName = "removed_" + kvSubject.getIdentifier() + "_" + getCurrentDateTime();
            
            // Rename the table
            String renameTableSQL = "ALTER TABLE " + kvSubject.getIdentifier() + " RENAME TO " + newTableName;
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(renameTableSQL);
                System.out.println("Table " + kvSubject.getIdentifier() + " renamed to " + newTableName);
            } catch (SQLException e) {
                e.printStackTrace(); // Handle exceptions
                return false; // Return false if there's an error
            }
        } 

        // Now, remove the subject from KVSubjects
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
        String selectSQL = "SELECT description, nextId, fieldTypeMap FROM KVSubjects WHERE identifier = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, identifier);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String description = rs.getString("description");
                int nextId = rs.getInt("nextId");
                String fieldTypeMapJson = rs.getString("fieldTypeMap");

                KVSubject kvSubject = new KVSubject(identifier, description);
                kvSubject.setNextId(nextId); // Set nextId

                // Deserialize fieldTypeMap from JSON
                if (fieldTypeMapJson != null) {
                    kvSubject.setFieldTypeMap(deserializeFieldTypeMap(fieldTypeMapJson));
                }

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
        String selectAllSQL = "SELECT identifier, description, nextId, fieldTypeMap FROM KVSubjects";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectAllSQL)) {
            while (rs.next()) {
                String identifier = rs.getString("identifier");
                String description = rs.getString("description");
                int nextId = rs.getInt("nextId");
                String fieldTypeMapJson = rs.getString("fieldTypeMap");

                KVSubject kvSubject = new KVSubject(identifier, description);
                kvSubject.setNextId(nextId); // Set nextId

                // Deserialize fieldTypeMap from JSON
                if (fieldTypeMapJson != null) {
                    kvSubject.setFieldTypeMap(deserializeFieldTypeMap(fieldTypeMapJson));
                }

                subjects.add(kvSubject);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions
        }
        return subjects; // Return list of KVSubjects
    }

    // Method to serialize fieldTypeMap to JSON string
    private String serializeFieldTypeMap(Map<String, KVObjectField> fieldTypeMap) {
        JSONObject jsonObject = new JSONObject();
        for (KVObjectField field : fieldTypeMap.values()) {
            JSONObject fieldObject = new JSONObject();
            fieldObject.put("field", field.getField());
            fieldObject.put("type", field.getType());
            fieldObject.put("mandatory", field.isMandatory());
            fieldObject.put("modifier", field.getModifier());
            fieldObject.put("defaultValue", field.getDefaultValue());
            jsonObject.put(field.getField(), fieldObject);
        }
        return jsonObject.toString();
    }

    // Method to deserialize fieldTypeMap from JSON string
    private Map<String, KVObjectField> deserializeFieldTypeMap(String jsonString) {
        Map<String, KVObjectField> fieldTypeMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject(jsonString);
        for (String key : jsonObject.keySet()) {
            JSONObject fieldObject = jsonObject.getJSONObject(key);
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
    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions
        } finally {
            // Optional: nullify the object to free memory
            connection = null;
        }
    }

    // Getter for the connection if needed
    public Connection getConnection() {
        return connection;
    }
}
