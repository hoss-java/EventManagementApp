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

public class OrganizeObjectHandler extends ObjectHandler {
    private static String emObjectId = "OrganizeObject";
    private List<EMObject> organizes;

    private int nextId; // Counter for generating unique IDs
    private static Map<String, EMObjectField> fieldTypeMap = new HashMap<>();

    public OrganizeObjectHandler() {
        super();
        this.organizes = new ArrayList<>();
        this.nextId = 1; // Start ID from 1

        initializeFieldTypeMap();
        initializeCommands();
    }

    private static void initializeFieldTypeMap() {
        // Populate the field type mappings
        //type, mandatory, modifier, default
        fieldTypeMap.put("id", new EMObjectField("int", true, "auto", "1"));
        fieldTypeMap.put("eventid", new EMObjectField("int", true, "user",""));
        fieldTypeMap.put("participantid", new EMObjectField("int", false,"user", ""));
    }

    // Method to initialize commands and their associated methods
    private void initializeCommands() {
        try {
            commandMap.put("addorganize", this.getClass().getDeclaredMethod("addOrganizeFromArgs",JSONObject.class));
            commandMap.put("listallorganizes", this.getClass().getDeclaredMethod("listOrganizes",JSONObject.class)); 
            // Add more commands here
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    // Register a participant via Organize
    public JSONObject addOrganize(EMObject organize) {
        organizes.add(organize);
        return ResponseHelper.createResponse("Organize added successfully", null);
    }

    // Remove a registration by participant name
    public JSONObject addOrganizeFromArgs(String name) {
        if (organizes.removeIf(organize -> ((String)  organize.getFieldValue("name")).equals(name))) {
            return ResponseHelper.createResponse("Organize removed successfully", null);
        } else {
            return ResponseHelper.createResponse("Organize not found", null);
        }
    }

    // Helper method to register a participant to an event via organize from args
    public JSONObject registerParticipantFromArgs(JSONObject args) {
        try {
            // Initialize a map to store participant fields
            Map<String, String> organizeFields = new HashMap<>();

            // Loop through fieldTypeMap to validate and populate organizeFields
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

                // Add value to organizeFields
                organizeFields.put(fieldName, value);
            }

            // Create the EMObject
            EMObject organize = new EMObject(emObjectId, fieldTypeMap, organizeFields);
            addOrganize(organize); // Add the organize to the list

            return ResponseHelper.createResponse("Participant registered successfully", new JSONObject().put("uniqueId", (int) organize.getFieldValue("id")));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseHelper.createResponse("Organize processing arguments: " + e.getMessage(), null);
        }
    }

   public JSONObject listOrganizes(JSONObject args) {
        JSONArray organizesArray = new JSONArray();

        if (organizes.isEmpty()) {
            return ResponseHelper.createResponse("No registered participant or event found.", null); // Response for an empty list
        }

        try {
            for (EMObject organize : organizes) {
                JSONObject organizeJson = new JSONObject();
                for (Map.Entry<String, EMObjectField> entry : fieldTypeMap.entrySet()) {
                    String fieldName = entry.getKey();
                    String valueStr = organize.getFieldValue(fieldName).toString();
                    organizeJson.put(fieldName, valueStr);
                }
                organizesArray.put(organizeJson);
            }
            JSONObject response = new JSONObject();
            response.put("organizes", organizesArray);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseHelper.createResponse("Error processing arguments: " + e.getMessage(), null);
        }
        JSONObject response = new JSONObject();
        response.put("organizes", organizesArray);
        return ResponseHelper.createResponse("Organizes listed successfully", response);
    }
}
