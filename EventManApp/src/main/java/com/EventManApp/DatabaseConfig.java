package com.EventManApp;

import java.io.InputStream;
import java.util.Properties;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import com.EventManApp.EncryptionUtil;

public class DatabaseConfig {
    private String sqlUrl;
    private String sqlDatabase;
    private String sqlUsername;
    private String sqlEncryptedPassword;
    private SecretKey sqlSecretKey;

    private String mongoAddress;
    private String mongoPort;
    private String mongoDatabase;
    private String mongoUsername;
    private String mongoEncryptedPassword;
    private SecretKey mongoSecretKey;


    public DatabaseConfig(String configFile) {
        loadProperties(configFile);
    }

    private void loadProperties(String configFile) {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                System.out.println("Sorry, unable to find db.properties");
                return;
            }

            // Load properties file
            props.load(input);

            // Get the properties
            this.sqlUrl = props.getProperty("db.sqlUrl");
            this.sqlDatabase = props.getProperty("db.sqldatabase");
            this.sqlUsername = props.getProperty("db.sqlusername");
            this.sqlEncryptedPassword = props.getProperty("db.sqlpassword");
            
            // Decode the secret key
            String keyString = props.getProperty("db.sqlsecretKey"); // Ensure to add this in your .properties file
            this.sqlSecretKey = stringToKey(keyString);

            // Get the properties
            this.mongoAddress = props.getProperty("db.mongoaddress");
            this.mongoPort = props.getProperty("db.mongoport");
            this.mongoDatabase = props.getProperty("db.mongodatabase");
            this.mongoUsername = props.getProperty("db.mongousername");
            this.mongoEncryptedPassword = props.getProperty("db.mongopassword");
            
            // Decode the secret key
            keyString = props.getProperty("db.mongosecretKey"); // Ensure to add this in your .properties file
            this.mongoSecretKey = stringToKey(keyString);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SecretKey stringToKey(String keyStr) {
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    // Decrypt the stored encrypted password
    public String getSqlDecryptedPassword() throws Exception {
        if (sqlEncryptedPassword == null || sqlSecretKey == null) {
            return null;
        }
        return EncryptionUtil.decrypt(sqlEncryptedPassword, sqlSecretKey);
    }
    
    // Getter methods
    public String getSqlUrl() {
        return sqlUrl+"/"+sqlDatabase;
    }

    public String getSqlDatabase() {
        return sqlDatabase;
    }

    public String getSqlUsername() {
        return sqlUsername;
    }

    // Decrypt the stored encrypted password
    public String getMongoDecryptedPassword() throws Exception {
        if (mongoEncryptedPassword == null || mongoSecretKey == null) {
            return null;
        }
        return EncryptionUtil.decrypt(mongoEncryptedPassword, mongoSecretKey);
    }

    // Getter methods
    public String getMongoAddress() {
        return mongoAddress;
    }

    public String getMongoPort() {
        return mongoPort;
    }

    public String getMongoDatabase() {
        return mongoDatabase;
    }

    public String getMongoUsername() {
        return mongoUsername;
    }
}
