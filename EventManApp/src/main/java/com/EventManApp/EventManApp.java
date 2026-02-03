package com.EventManApp;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Scanner;

import com.EventManApp.lib.JSONHelper;
import com.EventManApp.lib.ResponseHelper;

import com.EventManApp.lib.interfaces.ConsoleInterface;

import com.EventManApp.MenuCallback;

import com.EventManApp.ObjectHandler;
import com.EventManApp.EventObjectHandler;
import com.EventManApp.ParticipantObjectHandler;

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
        EventObjectHandler eventObjectHandler = new EventObjectHandler();
        ParticipantObjectHandler participantObjectHandler = new ParticipantObjectHandler();

        // Add them to a list
        List<ObjectHandler> objectHandlers = new ArrayList<>();
        objectHandlers.add(eventObjectHandler);
        objectHandlers.add(participantObjectHandler);

        MenuCallback callback = (callerID, menuItem) -> {
            logBuffer.append(callerID).append(": ").append(menuItem).append("\n");

            JSONObject selectedCommand = new JSONObject(menuItem);
            String commandId = selectedCommand.getString("id");
            JSONObject jsonResponse = null;

            // Iterate through each CommandHandler instance
            for (ObjectHandler handler : objectHandlers) {
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

    /**
     * Launches an interactive event managment that reads commands from
     * standard input, evaluates and run them,and prints results.
     *
     * @param args Command-line arguments (ignored).
     */
    public static void eventManager(InputStream in, PrintStream out, String[] args, MenuCallback callback) {
        JSONObject commands= JSONHelper.loadJsonFromFile("commands.json");

        ConsoleInterface myConsoleInterface = new ConsoleInterface(callback);
        JSONObject result = myConsoleInterface.executeCommands(commands);
        myConsoleInterface.close();
    }
}
