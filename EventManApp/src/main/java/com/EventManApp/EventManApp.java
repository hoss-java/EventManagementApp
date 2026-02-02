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

import com.EventManApp.lib.ConsoleCommand;

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

    /**
     * @brief Program entry point.
     *
     * @param args Command-line arguments (ignored by this application).
     */
    public static void main(String[] args) {
        eventManager(System.in, System.out,args);
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
    public static void eventManager(InputStream in, PrintStream out, String[] args) {
        JSONObject commands= loadJsonFromFile("commands.json");
        //System.out.println(commandJson);
        //JSONObject commands = new JSONObject(commandJson);
        //traverseAndPrint(commands,""); 

        ConsoleCommand myConsoleCommand = new ConsoleCommand();


        while (true) {
            JSONObject result = myConsoleCommand.executeCommands(commands);
            System.out.println("Final Command JSON: " + result.toString());
            break;
        }

        myConsoleCommand.close();
    }
}
