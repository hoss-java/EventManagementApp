package com.EventManApp;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Scanner;

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

    /**
     * Launches an interactive event managment that reads commands from
     * standard input, evaluates and run them,and prints results.
     *
     * @param args Command-line arguments (ignored).
     */
    public static void eventManager(InputStream in, PrintStream out, String[] args) {
        Scanner scanner = new Scanner(in);
        String input;

        out.println("Welcome to the Interactive Event Manager!");
        out.println("Type 'exit' to quit:");

        while (true) {
            out.print("> ");
            if (!scanner.hasNextLine()) break;
            input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }
        }

        scanner.close();
        out.println("Thank you for using. Goodbye!");
    }        
}
