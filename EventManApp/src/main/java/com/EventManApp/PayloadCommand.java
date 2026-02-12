package com.EventManApp;

import org.json.JSONObject;

import java.util.Map;

import com.EventManApp.PayloadDetail;
import com.EventManApp.lib.DebugUtil;

/**
 * Represents a command payload, including an ID, optional data, 
 * and optional arguments defined as PayloadDetail instances.
 */
public class PayloadCommand {
    private String id; // Required for each command
    private Map<String, Object> data; // Optional
    private Map<String, PayloadDetail> args; // Optional

    // Getters

    /**
     * Gets the command ID.
     * 
     * @return the command ID, which is required
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the optional data map.
     * 
     * @return a map of data associated with the command, or null if not set
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Gets the optional arguments map.
     * 
     * @return a map of arguments (PayloadDetail) associated with the command, or null if not set
     */
    public Map<String, PayloadDetail> getArgs() {
        return args;
    }

    // Setters

    /**
     * Sets the command ID.
     * 
     * @param id the ID to set for this command
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the optional data map.
     * 
     * @param data the data to associate with this command
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * Sets the optional arguments map.
     * 
     * @param args the arguments to associate with this command
     */
    public void setArgs(Map<String, PayloadDetail> args) {
        this.args = args;
    }

    /**
     * Converts this PayloadCommand instance to a JSON object.
     * 
     * @return a JSONObject representing the PayloadCommand instance
     */
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

    /**
     * Returns a string representation of this PayloadCommand instance in JSON format.
     * 
     * @return a JSON string representation of this instance
     */
    @Override
    public String toString() {
        return toJSON().toString();
    }
}
