package com.EventManApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import com.EventManApp.lib.DebugUtil;
import com.EventManApp.PayloadCommand;

public class Payload {
    private String identifier;
    private Map<String, Object> data;
    private List<PayloadCommand> commands;

    // Getters
    public String getIdentifier() {
        return identifier;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public List<PayloadCommand> getCommands() {
        return commands;
    }

    // Setters
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void setCommands(List<PayloadCommand> commands) {
        this.commands = commands;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        // Add identifier
        jsonObject.put("identifier", identifier);

        // Create JSON object for data map
        if (data != null) {
            JSONObject dataJson = new JSONObject();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                dataJson.put(entry.getKey(), entry.getValue());
            }
            jsonObject.put("data", dataJson);
        }

        // Create JSON array for commands list
        if (commands != null) {
            JSONArray commandsJson = new JSONArray();
            for (PayloadCommand command : commands) {
                commandsJson.put(command.toJSON()); // Assuming PayloadCommand has a toJSON method
            }
            jsonObject.put("commands", commandsJson);
        }

        return jsonObject;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
