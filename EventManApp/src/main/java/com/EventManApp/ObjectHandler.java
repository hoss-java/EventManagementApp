package com.EventManApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.EventManApp.lib.ResponseHelper;
import com.EventManApp.ActionCallbackInterface;

public class ObjectHandler implements ObjectHandlerInterface {
    protected String emObjectId = "NoId";
    protected final ActionCallbackInterface callback;
    protected Map<String, Method> commandMap;

    public ObjectHandler(ActionCallbackInterface callback) {
        this.callback = callback;
        this.commandMap = new HashMap<>();
        }

    // Getter and Setter for runInBackground
    public String getObjectId() {
        return emObjectId;
    }

    public void setObjectId(String objectId) {
        this.emObjectId = objectId;
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
        return parseCommands(commandsArray); // Call to second version
    }

    @Override
    public JSONObject parseCommands(JSONArray commandsArray) { // Change parameter type to JSONArray
        JSONObject response = new JSONObject();

        for (int i = 0; i < commandsArray.length(); i++) {
            JSONObject command = commandsArray.getJSONObject(i);
            String id = command.getString("id");
            JSONObject args = command.getJSONObject("args");
            JSONObject argsattributes = command.getJSONObject("argsattributes");

            if (isValidCommand(id)) {
                response = executeCommand(id, args,argsattributes);
            } else {
                response = ResponseHelper.createResponse("Unknown command: " + id, null);
            }
        }
        
        return response;
    }

    // Execute command based on command ID
    private JSONObject executeCommand(String commandId, JSONObject args,JSONObject argsattributes) {
        try {
            Method method = commandMap.get(commandId);
            if (method != null) {
                // If args is null, invoke the method with no arguments
                if (args == null) {
                    return (JSONObject) method.invoke(this);
                // If argsattributes is null, invoke the method without argsattributes
                } else if (argsattributes == null ) {
                    return (JSONObject) method.invoke(this, args);
                }else {
                    return (JSONObject) method.invoke(this, args, argsattributes);
                }
            }
        } catch (Exception e) {
            return ResponseHelper.createResponse("Error executing command: " + e.getMessage(), null);
        }
        return ResponseHelper.createResponse("Command execution failed", null);
    }
}