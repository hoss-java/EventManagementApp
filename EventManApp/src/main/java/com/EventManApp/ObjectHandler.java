package com.EventManApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.EventManApp.lib.ResponseHelper;

public class ObjectHandler implements ObjectHandlerInterface {
    protected Map<String, Method> commandMap;

    public ObjectHandler() {
            this.commandMap = new HashMap<>();
        }

    // Check if a command is valid
    @Override
    public boolean isValidCommand(String commandId) {
        return commandMap.containsKey(commandId);
    }

    // Command parser
    @Override
    public JSONObject parseCommands(String jsonCommands) {
        JSONArray commandsArray = new JSONArray(jsonCommands);
        JSONObject response = new JSONObject();

        for (int i = 0; i < commandsArray.length(); i++) {
            JSONObject command = commandsArray.getJSONObject(i);
            String id = command.getString("id");
            JSONObject args = command.getJSONObject("args");

            if (isValidCommand(id)) {
                response = executeCommand(id, args);
            } else {
                response = ResponseHelper.createResponse("Unknown command: " + id, null);
            }
        }
        
        return response;
    }

    // Execute command based on command ID
    private JSONObject executeCommand(String commandId, JSONObject args) {
        try {
            Method method = commandMap.get(commandId);
            if (method != null) {
                // If args is null, invoke the method with no arguments
                if (args == null) {
                    return (JSONObject) method.invoke(this);
                } else {
                    return (JSONObject) method.invoke(this, args);
                }
            }
        } catch (Exception e) {
            return ResponseHelper.createResponse("Error executing command: " + e.getMessage(), null);
        }
        return ResponseHelper.createResponse("Command execution failed", null);
    }
}