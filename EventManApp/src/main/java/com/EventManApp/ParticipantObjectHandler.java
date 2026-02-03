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

public class ParticipantObjectHandler extends ObjectHandler {
    private static String emObjectId = "ParticipantObject";
    private List<EMObject> participants;

    private int nextId; // Counter for generating unique IDs
    private static Map<String, EMObjectField> fieldTypeMap = new HashMap<>();

    public ParticipantObjectHandler() {
        super();
        this.participants = new ArrayList<>();
        this.nextId = 1; // Start ID from 1

        initializeFieldTypeMap();
        initializeCommands();
    }

    private static void initializeFieldTypeMap() {
        // Populate the field type mappings
        //type, mandatory, modifier, default
        fieldTypeMap.put("id", new EMObjectField("int", true, "auto", "1"));
        fieldTypeMap.put("name", new EMObjectField("str", true, "user",""));
        fieldTypeMap.put("email", new EMObjectField("str", false,"user", ""));
    }

    // Method to initialize commands and their associated methods
    private void initializeCommands() {
        try {
            commandMap.put("addparticipant", this.getClass().getDeclaredMethod("addParticipantFromArgs",JSONObject.class));
            commandMap.put("listallparticipants", this.getClass().getDeclaredMethod("listParticipants",JSONObject.class)); 
            // Add more commands here
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    // Add a participant
    public JSONObject addParticipant(EMObject participant) {
        participants.add(participant);
        return ResponseHelper.createResponse("Participant added successfully", null);
    }

    // Remove a participant by name
    public JSONObject removeParticipant(String name) {
        if (participants.removeIf(participant -> ((String)  participant.getFieldValue("name")).equals(name))) {
            return ResponseHelper.createResponse("Participant removed successfully", null);
        } else {
            return ResponseHelper.createResponse("Participant not found", null);
        }
    }

    // Helper method to create a participant from args
    public JSONObject addParticipantFromArgs(JSONObject args) {
        try {
            // Initialize a map to store participant fields
            Map<String, String> participantFields = new HashMap<>();

            // Loop through fieldTypeMap to validate and populate participantFields
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

                // Add value to participantFields
                participantFields.put(fieldName, value);
            }

            // Create the EMObject
            EMObject participant = new EMObject(emObjectId, fieldTypeMap, participantFields);
            addParticipant(participant); // Add the participant to the list

            return ResponseHelper.createResponse("Participant added successfully", new JSONObject().put("uniqueId", (int) participant.getFieldValue("id")));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseHelper.createResponse("Participant processing arguments: " + e.getMessage(), null);
        }
    }

   public JSONObject listParticipants(JSONObject args) {
        JSONArray participantsArray = new JSONArray();

        if (participants.isEmpty()) {
            return ResponseHelper.createResponse("No participants found.", null); // Response for an empty list
        }

        try {
            for (EMObject participant : participants) {
                JSONObject participantJson = new JSONObject();
                for (Map.Entry<String, EMObjectField> entry : fieldTypeMap.entrySet()) {
                    String fieldName = entry.getKey();
                    String valueStr = participant.getFieldValue(fieldName).toString();
                    participantJson.put(fieldName, valueStr);
                }
                participantsArray.put(participantJson);
            }
            JSONObject response = new JSONObject();
            response.put("participants", participantsArray);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseHelper.createResponse("Error processing arguments: " + e.getMessage(), null);
        }
        JSONObject response = new JSONObject();
        response.put("participants", participantsArray);
        return ResponseHelper.createResponse("Participants listed successfully", response);
    }
}
