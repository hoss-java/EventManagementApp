package com.EventManApp;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Scanner;

import com.EventManApp.lib.JSONHelper;
import com.EventManApp.lib.ResponseHelper;

import com.EventManApp.ResponseCallbackInterface;
import com.EventManApp.ActionCallbackInterface;

import com.EventManApp.ObjectHandler;
import com.EventManApp.LogHandler;

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
    private static boolean running = true;
    private static List<ObjectHandler> objectHandlerInstances = new ArrayList<>();
    private static List<BaseInterface> interfaceInstances = new ArrayList<>();
    private static LogHandler logHandler = new LogHandler();

    public static ResponseCallbackInterface responseHandler = (callerID, menuItem) -> {
        logHandler.addLog(callerID,"responseHandler",menuItem);

        JSONObject selectedCommand = new JSONObject(menuItem);
        String commandId = selectedCommand.getString("id");
        JSONObject jsonResponse = null;

        // Iterate through each CommandHandler instance
        for (ObjectHandler handler : objectHandlerInstances) {
            if (handler.isValidCommand(commandId)) {
                jsonResponse = handler.parseCommands("[" + menuItem + "]"); // Add array brackets
                break; // Exit loop if command is found
            }
        }

        // Handle invalid command
        if (jsonResponse == null) {
            jsonResponse = ResponseHelper.createInvalidCommandResponse(commandId);
        }

        return jsonResponse.toString();
    };

    public static ActionCallbackInterface actionHandler = (String callerID, JSONObject payload) -> {
        logHandler.addLog(callerID,"actionHandler",payload.toString());

        String commandId = payload.getString("id");
        JSONObject jsonResponse = null;

        // Iterate through each CommandHandler instance
        for (ObjectHandler handler : objectHandlerInstances) {
            if (handler.isValidCommand(commandId)) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(payload); // Add JSONObject to JSONArray
                jsonResponse = handler.parseCommands(jsonArray); // Add array brackets
                break; // Exit loop if command is found
            }
        }

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

            // Load handlers
            NodeList handlerList = doc.getElementsByTagName("handler");
            for (int i = 0; i < handlerList.getLength(); i++) {
                Element element = (Element) handlerList.item(i);
                String className = element.getTextContent();

                try {
                    // Load class dynamically
                    Class<?> clazz = Class.forName(className);
                    Object handlerInstance = clazz.getDeclaredConstructor(ActionCallbackInterface.class).newInstance(actionHandler);

                    // Check if instance is of type ObjectHandler before adding
                    if (handlerInstance instanceof ObjectHandler) {
                        objectHandlerInstances.add((ObjectHandler) handlerInstance);
                    } else {
                        System.err.println("Class " + className + " is not an instance of ObjectHandler.");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to instantiate handler class: " + className);
                    e.printStackTrace();
                }
            }

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

        JSONObject commands= JSONHelper.loadJsonFromFile("commands.json");
        loadModulesFromXML("modules.xml");

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

        scanner.close(); // Close the scanner at the end to free resources
        logHandler.displayLogs();
    }
}
