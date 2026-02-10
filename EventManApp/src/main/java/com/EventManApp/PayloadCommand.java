package com.EventManApp;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import com.EventManApp.lib.DebugUtil;
import com.EventManApp.PayloadDetail;

public class PayloadCommand {
    private String id; // Required for each command
    private Map<String, Object> data; // Optional
    private Map<String, PayloadDetail> args; // Optional

    // Getters
    public String getId() {
        return id;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Map<String, PayloadDetail> getArgs() {
        return args;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void setArgs(Map<String, PayloadDetail> args) {
        this.args = args;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", this.id); // Add ID to JSON

        // Convert data map to JSON object
        if (data != null) {
            JSONObject dataJson = new JSONObject(data); // Convert data map directly to JSONObject
            jsonObject.put("data", dataJson);
        }

        // Convert args map to JSON object
        if (args != null) {
            JSONObject argsJson = new JSONObject();
            for (Map.Entry<String, PayloadDetail> entry : args.entrySet()) {
                // Assuming PayloadDetail has a toJSON method
                argsJson.put(entry.getKey(), entry.getValue().toJSON()); 
            }
            jsonObject.put("args", argsJson);
        }

        return jsonObject;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
