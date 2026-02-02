package com.EventManApp;

import org.json.JSONObject;

public interface ObjectHandler {
    JSONObject parseCommands(String jsonCommands);
    boolean isValidCommand(String commandId);
}