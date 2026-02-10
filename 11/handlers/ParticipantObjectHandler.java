package com.EventManApp.handlers;

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

import com.EventManApp.ActionCallbackInterface;
import com.EventManApp.lib.ResponseHelper;
import com.EventManApp.lib.JSONHelper;
import com.EventManApp.lib.StringParserHelper;

import com.EventManApp.EMObjectField;
import com.EventManApp.EMObject;
import com.EventManApp.ObjectHandler;
import com.EventManApp.ValueComparator;

public class ParticipantObjectHandler extends ObjectHandler {
    private List<EMObject> participants;

    private int nextId; // Counter for generating unique IDs
    private static Map<String, EMObjectField> fieldTypeMap = new HashMap<>();

    public ParticipantObjectHandler(ActionCallbackInterface callback) {
        super(callback);
        setObjectId("ParticipantObject");
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
            commandMap.put("addparticipant", this.getClass().getDeclaredMethod("addParticipantFromArgs",JSONObject.class,JSONObject.class));
            commandMap.put("removeparticipantbyname", this.getClass().getDeclaredMethod("removeParticipantByName",JSONObject.class,JSONObject.class));
            commandMap.put("removeparticipantbyid", this.getClass().getDeclaredMethod("removeParticipantByName",JSONObject.class,JSONObject.class));
            commandMap.put("listparticipants", this.getClass().getDeclaredMethod("listParticipants",JSONObject.class,JSONObject.class)); 
            // Add more commands here
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    // Add a participant
    public JSONObject addParticipant(EMObject participant) {
        // Validate before adding
        if (EMObject.isValidForAddition(participants, participant)) {
            participants.add(participant);
            return ResponseHelper.createResponse("Participant added successfully", null, RESPONSE_DEFAULT_VERSION);
        } else {
            throw new IllegalArgumentException("An EMObject with the same non-id fields already exists.");
        }
    }

    // Remove a participant by name
    public JSONObject removeParticipant(int id) {
        if (participants.removeIf(participant -> {
             Integer participantId = (int) participant.getFieldValue("id");
            return participantId != null && participantId.equals(id);
        })) {
            return ResponseHelper.createResponse("Participant removed successfully", null);
        }
        return ResponseHelper.createResponse("Participant not found", null);
    }

    // Helper method to create an event from args
    public JSONObject addParticipantFromArgs() {
        return addParticipantFromArgs(null, null);
    }

    public JSONObject addParticipantFromArgs(JSONObject args) {
        return addParticipantFromArgs(args, null);
    }

    public JSONObject addParticipantFromArgs(JSONObject args, JSONObject argsattributes) {
        if (args == null){
            return ResponseHelper.createResponse("Error: No arguments were provided!", null);
        }

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

            return ResponseHelper.createResponse("Participant added successfully", new JSONObject().put("id", (int) participant.getFieldValue("id")));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseHelper.createResponse("Participant processing arguments: " + e.getMessage(), null);
        }
    }

    public JSONObject removeParticipantByName() {
        return removeParticipantByName(null, null);
    }

    public JSONObject removeParticipantByName(JSONObject args) {
        return removeParticipantByName(args, null);
    }

    public JSONObject removeParticipantByName(JSONObject args, JSONObject argsattributes) {
        if (args != null) {
            int participantid = findId(args, new String[][] {
                {"id", "id"},
                {"name", "name"}},
                "listparticipants"
                );

            if ( participantid != -1 ){
                try {
                    return removeParticipant(participantid); // Add the organize to the list
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return ResponseHelper.createResponse("Event processing arguments: " + e.getMessage(), null,RESPONSE_DEFAULT_VERSION);
                }
            }
        }
        return ResponseHelper.createResponse("Error: Invalid or/No arguments were provided!", null,RESPONSE_DEFAULT_VERSION);

    }

    public JSONObject listParticipants() {
        return listParticipants(null, null);
    }

    public JSONObject listParticipants(JSONObject args) {
        return listParticipants(args, null);
    }

    public JSONObject listParticipants(JSONObject args, JSONObject argsattributes) {
        JSONArray participantsArray = new JSONArray();

        if (participants.isEmpty()) {
            return ResponseHelper.createResponse("No participants found.", null); // Response for an empty list
        }

        try {
            for (EMObject participant : participants) {
                JSONObject participantJson = new JSONObject();
                boolean matchedParticipant = true;
                for (Map.Entry<String, EMObjectField> entry : fieldTypeMap.entrySet()) {
                    String fieldName = entry.getKey();
                    EMObjectField definition = entry.getValue();
                    String valueStr = participant.getFieldValue(fieldName).toString();
                    String compareStr = args.optString(fieldName);
                    if (compareStr != null &&  !compareStr.equals("")) {
                        compareStr = StringParserHelper.parseString(compareStr);
                        String compareMode = "=";
                        if ( argsattributes != null){
                            JSONObject compareAttr = JSONHelper.getJsonValue(argsattributes, "fieldName");
                            if (compareAttr != null){
                                compareMode = compareAttr.optString("compareMode",compareMode);
                            }
                        }
                        if ( !ValueComparator.validateValue(valueStr,compareStr,definition.getType(),compareMode) ){
                            matchedParticipant = false;
                            break;
                        }
                        participantJson.put(fieldName, valueStr);
                    }
                    else {
                        participantJson.put(fieldName, valueStr);
                    }
                }
                if (matchedParticipant == true){
                    participantsArray.put(participantJson);
                }
            }
            JSONObject response = new JSONObject();
            response.put("data", participantsArray);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseHelper.createResponse("Error processing arguments: " + e.getMessage(), null);
        }
        JSONObject response = new JSONObject();
        response.put("data", participantsArray);
        return ResponseHelper.createResponse("Participants listed successfully", response);
    }
}
