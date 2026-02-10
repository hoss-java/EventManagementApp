package com.EventManApp;

import org.json.JSONArray;
import org.json.JSONObject;

import com.EventManApp.PayloadCommand;
import com.EventManApp.Payload;
import com.EventManApp.lib.DebugUtil;
import com.EventManApp.lib.ResponseHelper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    protected Map<String, Method> commandMap; // Commands mapping
    protected static final String CONSTANT_COMMAND_EXECUTION_FAILED = "Command execution failed"; // Constant 
    protected static final String CONSTANT_COMMAND_NOT_FOUND = "Command not found";
    protected static final String CONSTANT_ERROR_EXECUTING_COMMAND = "Error executing command";

    public CommandHandler() {
        this.commandMap = new HashMap<>();
        initializeDefaultCommands();
    }

    // Method to initialize commands and their associated methods
    private void initializeDefaultCommands() {
        try {
            commandMap.put("add", this.getClass().getMethod("addRow", String.class, PayloadCommand.class));
            commandMap.put("remove", this.getClass().getMethod("removeRow", String.class, PayloadCommand.class));
            commandMap.put("get", this.getClass().getMethod("getRow", String.class, PayloadCommand.class));
            commandMap.put("gets", this.getClass().getMethod("getRows", String.class, PayloadCommand.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public JSONObject addRow(String identifier, PayloadCommand command) {
        // Default implementation
        return ResponseHelper.createResponse(CONSTANT_COMMAND_EXECUTION_FAILED, null);
    }

    public JSONObject removeRow(String identifier, PayloadCommand command) {
        // Default implementation
        return ResponseHelper.createResponse(CONSTANT_COMMAND_EXECUTION_FAILED, null);
    }

    public JSONObject getRow(String identifier, PayloadCommand command) {
        // Default implementation
        return ResponseHelper.createResponse(CONSTANT_COMMAND_EXECUTION_FAILED, null);
    }

    public JSONObject getRows(String identifier, PayloadCommand command) {
        // Default implementation
        return ResponseHelper.createResponse(CONSTANT_COMMAND_EXECUTION_FAILED, null);
    }

    // Method to add a new command to the command map
    public void addCommand(String commandId, String methodName) {
        try {
            Method method = this.getClass().getMethod(methodName, String.class, PayloadCommand.class);
            commandMap.put(commandId, method);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    // Method to remove an existing command from the command map
    public void removeCommand(String commandId) {
        commandMap.remove(commandId);
    }

    // Method to get a command's associated method
    public Method getCommand(String commandId) {
        return commandMap.get(commandId);
    }
}