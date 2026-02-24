package com.EventManApp;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.json.JSONObject;

public abstract class BaseInterface {
    protected final ResponseCallbackInterface callback;
    private boolean runInBackground;
    private boolean running = true;
    protected final PrintStream out;
    protected final InputStream in;
    protected final boolean isNetworkStream;

    public BaseInterface(ResponseCallbackInterface callback, PrintStream out, InputStream in) {
        this.callback = callback;
        this.out = out;
        this.in = in;
        this.isNetworkStream = !in.getClass().getName().equals("java.io.BufferedInputStream");
    }

    public BaseInterface(ResponseCallbackInterface callback) {
        this(callback, System.out, System.in);
    }

    // Getter and Setter for runInBackground
    public boolean isRunInBackground() {
        return runInBackground;
    }

    public void setRunInBackground(boolean runInBackground) {
        this.runInBackground = runInBackground;
    }

    public void setRunningFlag(boolean running) {
        this.running = running;
    }

    public boolean getRunningFlag() {
        return this.running;
    }

    public void printJson(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        printJson(jsonObject);
    }

    public void print(String message) {
        this.out.print("\r" + message.replace("\n", "\r\n"));
        this.out.flush();
    }

    public void println(String message) {
        if (this.isNetworkStream) {
            this.out.print( "\r" + message.replace("\n", "\r\n") + "\r\n");
        } else {
            this.out.print(message + "\n");
        }
        this.out.flush();
    }

    public void println() {
        if (this.isNetworkStream) {
            this.out.print("\r\n");
        } else {
            this.out.print("\n");
        }
        this.out.flush();
    }

    public void printJson(JSONObject jsonObject) {
        // Print the JSON with indentation for readability
        String formattedJson = jsonObject.toString(2); // Indent with 2 spaces
        println("JSON Response:\n" + formattedJson);
    }

    // Abstract method to be implemented by subclasses
    public abstract JSONObject executeCommands(JSONObject commands);
}
