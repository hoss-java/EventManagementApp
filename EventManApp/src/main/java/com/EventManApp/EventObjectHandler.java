package com.EventManApp;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.EventManApp.lib.ResponseHelper;

import com.EventManApp.EMObjectField;
import com.EventManApp.EMObject;
import com.EventManApp.ObjectHandler;

public class EventObjectHandler extends ObjectHandler {
    private static String emObjectId = "EventObject";
    private List<EMObject> events;

    private int nextId; // Counter for generating unique IDs
    private static Map<String, EMObjectField> fieldTypeMap = new HashMap<>();

    public EventObjectHandler() {
        super();
        this.events = new ArrayList<>();
        this.nextId = 1; // Start ID from 1

        initializeFieldTypeMap();
        initializeCommands();
    }

    private static void initializeFieldTypeMap() {
        // Populate the field type mappings
        //type, mandatory, modifier, default
        fieldTypeMap.put("id", new EMObjectField("int", true, "auto", "1"));
        fieldTypeMap.put("title", new EMObjectField("str", true, "user","workshop"));
        fieldTypeMap.put("location", new EMObjectField("str", false,"user", "here"));
        fieldTypeMap.put("capacity", new EMObjectField("positiveInt", false,"user", "1")); // Default value for capacity
        fieldTypeMap.put("date", new EMObjectField("date", false,"user", LocalDate.now().toString())); // Default date
        fieldTypeMap.put("starttime", new EMObjectField("time", false,"user", "00:00:00")); // Default time
        fieldTypeMap.put("duration", new EMObjectField("duration", false,"user", "PT0H")); // Default duration
    }

    // Method to initialize commands and their associated methods
    private void initializeCommands() {
        try {
            commandMap.put("addevent", this.getClass().getDeclaredMethod("addEventFromArgs",JSONObject.class));
            commandMap.put("listallevents", this.getClass().getDeclaredMethod("listEvents",JSONObject.class)); 
            // Add more commands here
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    // Add an event
    public JSONObject addEvent(EMObject event) {
        events.add(event);
        return ResponseHelper.createResponse("Event added successfully", null);
    }

    // Remove an event by title
    public JSONObject removeEvent(String title) {
        if (events.removeIf(event -> ((String)  event.getFieldValue("title")).equals(title))) {
            return ResponseHelper.createResponse("Event removed successfully", null);
        } else {
            return ResponseHelper.createResponse("Event not found", null);
        }
    }

    // Helper method to create an event from args
    public JSONObject addEventFromArgs(JSONObject args) {
        try {
            // Initialize a map to store event fields
            Map<String, String> eventFields = new HashMap<>();

            // Loop through fieldTypeMap to validate and populate eventFields
            for (Map.Entry<String, EMObjectField> entry : fieldTypeMap.entrySet()) {
                String fieldName = entry.getKey();
                EMObjectField definition = entry.getValue();
                String value;
                
                if (definition.getModifier().equals("auto")){
                    value = Integer.toString(this.nextId);
                    this.nextId++;
                }
                else{
                    value = args.optString(fieldName, null);
                }
                // Check for mandatory fields
                if (definition.isMandatory() && value == null) {
                    return ResponseHelper.createResponse("Error: " + fieldName + " is a mandatory field.", null);
                }

                // Use default value if the field is not mandatory and is missing
                if (value == null && !definition.isMandatory() && !definition.getModifier().equals("auto")) {
                    value = definition.getDefaultValue();
                }

                // Add value to eventFields
                eventFields.put(fieldName, value);
            }

            // Create the EMObject
            EMObject event = new EMObject(emObjectId, fieldTypeMap, eventFields);
            addEvent(event); // Add the event to the list

            return ResponseHelper.createResponse("Event added successfully", new JSONObject().put("uniqueId", (int) event.getFieldValue("id")));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseHelper.createResponse("Error processing arguments: " + e.getMessage(), null);
        }
    }

    public JSONObject listEvents(JSONObject args) {
        JSONArray eventsArray = new JSONArray();

        if (events.isEmpty()) {
            return ResponseHelper.createResponse("No events found.", null); // Response for an empty list
        }

        try {
            for (EMObject event : events) {
                JSONObject eventJson = new JSONObject();
                for (Map.Entry<String, EMObjectField> entry : fieldTypeMap.entrySet()) {
                    String fieldName = entry.getKey();
                    String valueStr = event.getFieldValue(fieldName).toString();
                    eventJson.put(fieldName, valueStr);
                }
                eventsArray.put(eventJson);
            }
            JSONObject response = new JSONObject();
            response.put("events", eventsArray);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseHelper.createResponse("Error processing arguments: " + e.getMessage(), null);
        }
        JSONObject response = new JSONObject();
        response.put("events", eventsArray);
        return ResponseHelper.createResponse("Events listed successfully", response);
    }
}
