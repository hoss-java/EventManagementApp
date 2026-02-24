package com.EventManApp;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private String dataPath;

    public AppConfig(String configFile) {
        loadProperties(configFile);
    }

    private void loadProperties(String configFile) {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                System.out.println("Sorry, unable to find "+configFile);
                return;
            }

            // Load properties file
            props.load(input);

            // Get the properties
            this.dataPath = props.getProperty("app.datapath");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Getter methods
    public String getDataPath() {
        return dataPath;
    }
}
