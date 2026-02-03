package com.EventManApp.lib;

import org.json.JSONObject;

public class ResponseHelper {

    public ResponseHelper() {
    }

    public static JSONObject createInvalidCommandResponse(String commandId) {
        return new JSONObject().put("message", "Invalid command: " + commandId);
    }

    public static JSONObject createResponse(String message, JSONObject data) {
        JSONObject response = new JSONObject();
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        return response;
    }
}