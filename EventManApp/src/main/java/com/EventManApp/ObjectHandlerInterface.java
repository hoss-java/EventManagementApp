package com.EventManApp;

import org.json.JSONObject;

public interface ObjectHandlerInterface {
    JSONObject parseCommands(String jsonCommands);
    boolean isValidCommand(String commandId);
}