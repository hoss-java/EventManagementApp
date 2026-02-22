package com.EventManApp;

import org.json.JSONObject;

public abstract class BaseInterface {
    protected final ResponseCallbackInterface callback;
    private boolean runInBackground;
    private boolean running = true;

    public BaseInterface(ResponseCallbackInterface callback) {
        this.callback = callback;
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

    public void printJson(JSONObject jsonObject) {
        // Print the JSON with indentation for readability
        String formattedJson = jsonObject.toString(2); // Indent with 2 spaces
        System.out.println("JSON Response:\n" + formattedJson);
    }

    // Abstract method to be implemented by subclasses
    public abstract JSONObject executeCommands(JSONObject commands);
}
