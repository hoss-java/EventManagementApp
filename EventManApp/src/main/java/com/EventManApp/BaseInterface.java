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

    // Abstract method to be implemented by subclasses
    public abstract JSONObject executeCommands(JSONObject commands);
}
