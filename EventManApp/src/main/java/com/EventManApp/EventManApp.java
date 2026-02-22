package com.EventManApp;

import com.EventManApp.ActionCallbackInterface;
import com.EventManApp.ConfigManager;
import com.EventManApp.ResponseCallbackInterface;
import com.EventManApp.KVObjectStorageFactory;
import com.EventManApp.lib.JSONHelper;
import com.EventManApp.lib.ResponseHelper;
import com.EventManApp.LogHandler;
import com.EventManApp.storages.DatabaseKVObjectStorage;
import com.EventManApp.storages.FileKVObjectStorage;
import com.EventManApp.storages.MemoryKVObjectStorage;
import com.EventManApp.storages.DatabaseKVSubjectStorage;
import com.EventManApp.storages.FileKVSubjectStorage;
import com.EventManApp.storages.MemoryKVSubjectStorage;
import com.EventManApp.DatabaseConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @file EventManApp.java
 * @brief Interactive console event managment application.
 *
 * This class provides the program entry point for the event managment application.
 *
 * Features:
 * -
 */
public class EventManApp {
    private static final StringBuilder logBuffer = new StringBuilder();
    private boolean running = true;
    private static List<BaseInterface> interfaceInstances = new ArrayList<>();
    private static LogHandler logHandler = new LogHandler();
    private static KVObjectHandler kvObjectHandler;
    private static KVSubjectHandler kvSubjectHandler;
    private static PayloadHandler payloadHandler;

    public static ResponseCallbackInterface responseHandler = (callerID, menuItem) -> {
        //logHandler.addLog(callerID,"responseHandler",menuItem);

        String commandId = "selectedCommand.getString";
        JSONObject jsonResponse = null;

        jsonResponse = payloadHandler.parsePayload(menuItem);

        // Handle invalid command
        if (jsonResponse == null) {
            jsonResponse = ResponseHelper.createInvalidCommandResponse(commandId);
        }

        return jsonResponse.toString();
    };

    public static ActionCallbackInterface actionHandler = (String callerID, JSONObject payload) -> {
        logHandler.addLog(callerID,"actionHandler",payload.toString());

        String commandId = "selectedCommand.getString";
        JSONObject jsonResponse = null;

        // Handle invalid command
        if (jsonResponse == null) {
            jsonResponse = ResponseHelper.createInvalidCommandResponse(commandId);
        }
       return jsonResponse;
    };

    private static void loadModulesFromXML(String fileName) {
        try (InputStream inputStream = EventManApp.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("File not found: " + fileName);
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            // Load interfaces
            NodeList interfaceList = doc.getElementsByTagName("interface");
            for (int i = 0; i < interfaceList.getLength(); i++) {
                Element element = (Element) interfaceList.item(i);
                String interfaceName = element.getAttribute("name");
                boolean runInBackground = Boolean.parseBoolean(element.getAttribute("runInBackground"));

                try {
                    Class<?> clazz = Class.forName(interfaceName);
                    BaseInterface interfaceInstance = (BaseInterface) clazz.getDeclaredConstructor(ResponseCallbackInterface.class).newInstance(responseHandler);
                    // Set the runInBackground attribute
                    interfaceInstance.setRunInBackground(runInBackground);

                    // Add the instance to the list
                    interfaceInstances.add(interfaceInstance);
                } catch (Exception e) {
                    System.err.println("Failed to instantiate interface class: " + interfaceName);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @brief Program entry point.
     *
     * @param args Command-line arguments (ignored by this application).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Define the appdata folder path
        String appDataFolder = ".appdata";

        // Create the appdata folder if it doesn't exist
        File directory = new File(appDataFolder);
        if (!directory.exists()) {
            directory.mkdirs(); // Create the folder
            System.out.println("Created directory: " + appDataFolder);
        }

        ConfigManager configManager = ConfigManager.getInstance(".appdata/config.json");
        JSONObject commands= JSONHelper.loadJsonFromFile("commands.json");
        loadModulesFromXML("modules.xml");

        DatabaseConfig dbConfigFile = new DatabaseConfig("db.properties");

        KVObjectStorage objectStorage = KVObjectStorageFactory.createKVObjectStorage("mongodb", dbConfigFile);
        //KVObjectStorage objectStorage = KVObjectStorageFactory.createKVObjectStorage("database", dbConfigFile);
        // Create a FileKVObjectStorage instance
        //File objectStorageFile = new File(".appdata");
        //KVObjectStorage objectStorage = KVObjectStorageFactory.createKVObjectStorage("file", objectStorageFile);
        //KVObjectStorage objectStorage = KVObjectStorageFactory.createKVObjectStorage("memory", null);
        kvObjectHandler = new KVObjectHandler(null,objectStorage);

        KVSubjectStorage subjectStorage = KVSubjectStorageFactory.createKVSubjectStorage("mongodb", dbConfigFile);
        //KVSubjectStorage subjectStorage = KVSubjectStorageFactory.createKVSubjectStorage("database", dbConfigFile);
        //File subjectStorageFile = new File(".appdata/kvsubjects.txt");
        //KVSubjectStorage subjectStorage = KVSubjectStorageFactory.createKVSubjectStorage("file", subjectStorageFile);
        //KVSubjectStorage subjectStorage = KVSubjectStorageFactory.createKVSubjectStorage("memory", null);
        kvSubjectHandler = new KVSubjectHandler("subjects.xml",subjectStorage);
        payloadHandler = new PayloadHandler(kvObjectHandler,kvSubjectHandler);

        // List to keep track of background threads
        List<Thread> backgroundThreads = new ArrayList<>();

        // Check each interface and handle as background or foreground
        for (BaseInterface interfaceInstance : interfaceInstances) {
            if (interfaceInstance.isRunInBackground()) {
                // Start a new thread for each background interface
                Thread interfaceThread = new Thread(() -> {
                    // Execute commands for the background interface
                    JSONObject result = interfaceInstance.executeCommands(commands);
                // Print the class name along with the result
                System.out.println("Background Execution result from " + interfaceInstance.getClass().getSimpleName() + ": " + result);
                });

                backgroundThreads.add(interfaceThread);
                interfaceThread.start();
            }
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine(); // Wait for a key press
        // Check each interface and handle as background or foreground
        for (BaseInterface interfaceInstance : interfaceInstances) {
            if (!interfaceInstance.isRunInBackground()) {
                // Execute foreground interfaces one by one
                JSONObject result = interfaceInstance.executeCommands(commands);
                System.out.println("Foreground Execution result from " + interfaceInstance.getClass().getSimpleName() + ": " + result);
            }
        }

        // Stop all background threads gracefully
        for (BaseInterface backgroundInterface : interfaceInstances) {
            if (backgroundInterface.isRunInBackground()) {
                backgroundInterface.setRunningFlag(false); // Assuming you have a running flag in the interface
            }
        }

        // Wait for all background threads to complete
        for (Thread thread : backgroundThreads) {
            try {
                thread.join(); // Wait for the background thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        objectStorage.close();
        subjectStorage.close();
        scanner.close(); // Close the scanner at the end to free resources
        logHandler.displayLogs();
        configManager.saveConfig();
    }
}
