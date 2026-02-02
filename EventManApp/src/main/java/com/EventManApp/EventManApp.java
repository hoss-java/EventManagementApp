package com.EventManApp;

import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Scanner;

import com.EventManApp.lib.ConsoleInterface;
import com.EventManApp.MenuCallback;
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

    /**
     * @brief Program entry point.
     *
     * @param args Command-line arguments (ignored by this application).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MenuCallback callback = (callerID, menuItem) -> {
            logBuffer.append(callerID).append(": ").append(menuItem).append("\n");
            // Create a JSON object
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("menuItem", new JSONObject(menuItem));
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Menu item processed successfully");
            // Return the JSON string
            return jsonResponse.toString();
        };

        // Start the log display thread
        Thread logThread = new Thread(EventManApp::displayLogs);
        logThread.start();


        eventManager(System.in, System.out,args,callback);

        // Stop the log thread when done
        running = false;
        try {
            logThread.join(); // Wait for the log thread to finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        scanner.close(); // Close the scanner at the end to free resources
    }

    private static void displayLogs() {
        while (running) {
            // Print logs if any
            synchronized (logBuffer) {
                if (logBuffer.length() > 0) {
                    System.out.println("\n\n----------\nLogs:");
                    System.out.println(logBuffer.toString());
                    logBuffer.setLength(0); // Clear the log buffer after printing
                }
            }
            try {
                Thread.sleep(1000); // Update log display every second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // This method must be defined within the Main class
    private static JSONObject loadJsonFromFile(String fileName) {
        try (InputStream inputStream = EventManApp.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("File not found: " + fileName);
            }
            // Read the input stream into a string
            String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            // Create JSONObject using LinkedHashMap to preserve order
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            JSONObject jsonObject = new JSONObject(map);
            return new JSONObject(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void traverseAndPrint(JSONObject jsonObject, String parentKey) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            String fullKey = parentKey.isEmpty() ? key : parentKey + "." + key;

            if (value instanceof JSONObject) {
                // Recursive call for nested JSONObject
                traverseAndPrint((JSONObject) value, fullKey);
            } else {
                // Print the key and value
                System.out.println(fullKey + ": " + value);
            }
        }
    }

    /**
     * Launches an interactive event managment that reads commands from
     * standard input, evaluates and run them,and prints results.
     *
     * @param args Command-line arguments (ignored).
     */
    public static void eventManager(InputStream in, PrintStream out, String[] args, MenuCallback callback) {
        JSONObject commands= loadJsonFromFile("commands.json");

        ConsoleInterface myConsoleInterface = new ConsoleInterface(callback);
        JSONObject result = myConsoleInterface.executeCommands(commands);
        myConsoleInterface.close();
    }
}
