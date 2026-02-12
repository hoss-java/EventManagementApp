package com.EventManApp;

import java.io.InputStream;
import java.util.Properties;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import com.EventManApp.EncryptionUtil;

public class DatabaseConfig {
    private String url;
    private String database;
    private String username;
    private String encryptedPassword;
    private SecretKey secretKey;

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
            this.url = props.getProperty("db.url");
            this.database = props.getProperty("db.database");
            this.username = props.getProperty("db.username");
            this.encryptedPassword = props.getProperty("db.password");
            
            // Decode the secret key
            String keyString = props.getProperty("db.secretKey"); // Ensure to add this in your .properties file
            this.secretKey = stringToKey(keyString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SecretKey stringToKey(String keyStr) {
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    // Decrypt the stored encrypted password
    public String getDecryptedPassword() throws Exception {
        if (encryptedPassword == null || secretKey == null) {
            return null;
        }
        return EncryptionUtil.decrypt(encryptedPassword, secretKey);
    }
    
    // Getter methods
    public String getUrl() {
        return url+"/"+database;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }
}
