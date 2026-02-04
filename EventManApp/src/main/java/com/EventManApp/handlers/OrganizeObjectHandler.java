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

public class OrganizeObjectHandler extends ObjectHandler {
    private List<EMObject> organizes;

    private int nextId; // Counter for generating unique IDs
    private static Map<String, EMObjectField> fieldTypeMap = new HashMap<>();

    public OrganizeObjectHandler(ActionCallbackInterface callback) {
        super(callback);
        setObjectId("OrganizeObject");
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
            commandMap.put("addorganizebyid", this.getClass().getDeclaredMethod("addOrganizeFromArgs",JSONObject.class,JSONObject.class));
            commandMap.put("addorganizebyname", this.getClass().getDeclaredMethod("addOrganizeFromArgs",JSONObject.class,JSONObject.class));
            commandMap.put("removeorganizebyid", this.getClass().getDeclaredMethod("removeOrganizeById",JSONObject.class,JSONObject.class));
            commandMap.put("removeparticipantfromorganize", this.getClass().getDeclaredMethod("removePaticipantFromOrganizeById",JSONObject.class,JSONObject.class));
            commandMap.put("listorganizes", this.getClass().getDeclaredMethod("listOrganizes",JSONObject.class,JSONObject.class)); 
            // Add more commands here
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    // Register a participant via Organize
    public JSONObject addOrganize(EMObject organize) {
        // Validate before adding
        if (EMObject.isValidForAddition(organizes, organize)) {
            organizes.add(organize);
            return ResponseHelper.createResponse("Organize added successfully", null, RESPONSE_DEFAULT_VERSION);
        } else {
            throw new IllegalArgumentException("An EMObject with the same non-id fields already exists.");
        }
    }

    // Remove a registration by Organize name
    public JSONObject removeOrganize(int id) {
        if (organizes.removeIf(organize -> {
             Integer organizeId = (int) organize.getFieldValue("id");
            return organizeId != null && organizeId.equals(id);
        })) {
            return ResponseHelper.createResponse("Organize removed successfully", null);
        }
        return ResponseHelper.createResponse("Organize not found", null);
    }

    public JSONObject removeParticipantFromOrganize(int participantid) {
        if (organizes.removeIf(organize -> {
            Integer organizeId = (int) organize.getFieldValue("id");
            Integer participantId = (int) organize.getFieldValue("participantid");
            return organizeId != null && participantId.equals(participantId);
        })) {
            return ResponseHelper.createResponse("Organizes(for participant) removed successfully", null);
        }
        return ResponseHelper.createResponse("Organize not found", null);
    }


    // Helper method to create an event from args
    public JSONObject addOrganizeFromArgs() {
        return addOrganizeFromArgs(null, null);
    }

    public JSONObject addOrganizeFromArgs(JSONObject args) {
        return addOrganizeFromArgs(args, null);
    }

    public JSONObject addOrganizeFromArgs(JSONObject args, JSONObject argsattributes) {
        if (args != null) {
            int eventid = findId(args, new String[][] {
                {"eventid", "id"},
                {"eventtitle", "title"}},
                "listevents"
                );

            int participantid = findId(args, new String[][] {
                {"participantid", "id"},
                {"participantname", "name"}},
                "listparticipants"
                );

            if ( eventid != -1 && participantid != -1){
                JSONHelper.updateJSONObject(args, new String[][] {
                    {"eventid", Integer.toString(eventid)},
                    {"participantid",  Integer.toString(participantid)}}
                    );

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
                            return ResponseHelper.createResponse("Error: " + fieldName + " is a mandatory field.", null,RESPONSE_DEFAULT_VERSION);
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

                    return ResponseHelper.createResponse("Participant registered successfully", new JSONObject().put("id", (int) organize.getFieldValue("id")),RESPONSE_DEFAULT_VERSION);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return ResponseHelper.createResponse("Organize processing arguments: " + e.getMessage(), null,RESPONSE_DEFAULT_VERSION);
                }
            }
        }
        return ResponseHelper.createResponse("Error: Invalid or/No arguments were provided!", null,RESPONSE_DEFAULT_VERSION);
    }

    public JSONObject removeOrganizeById() {
        return removeOrganizeById(null, null);
    }

    public JSONObject removeOrganizeById(JSONObject args) {
        return removeOrganizeById(args, null);
    }

    public JSONObject removeOrganizeById(JSONObject args, JSONObject argsattributes) {
        if (args != null) {
            int organizeid = findId(args, new String[][] {
                {"id", "id"}},
                "listorganizes"
                );

            if ( organizeid != -1 ){
                try {
                    return removeOrganize(organizeid); // Add the organize to the list
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return ResponseHelper.createResponse("Event processing arguments: " + e.getMessage(), null,RESPONSE_DEFAULT_VERSION);
                }
            }
        }
        return ResponseHelper.createResponse("Error: Invalid or/No arguments were provided!", null,RESPONSE_DEFAULT_VERSION);
    }

    public JSONObject removePaticipantFromOrganizeById() {
        return removePaticipantFromOrganizeById(null, null);
    }

    public JSONObject removePaticipantFromOrganizeById(JSONObject args) {
        return removePaticipantFromOrganizeById(args, null);
    }

    public JSONObject removePaticipantFromOrganizeById(JSONObject args, JSONObject argsattributes) {
        if (args != null) {
            int participantid = findId(args, new String[][] {
                {"id", "id"},
                {"name", "name"}},
                "listparticipants"
                );

            if ( participantid != -1 ){
                try {
                    return removeParticipantFromOrganize(participantid); // Add the organize to the list
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return ResponseHelper.createResponse("Event processing arguments: " + e.getMessage(), null,RESPONSE_DEFAULT_VERSION);
                }
            }
        }
        return ResponseHelper.createResponse("Error: Invalid or/No arguments were provided!", null,RESPONSE_DEFAULT_VERSION);
    }

    public JSONObject listOrganizes() {
        return listOrganizes(null, null);
    }

    public JSONObject listOrganizes(JSONObject args) {
        return listOrganizes(args, null);
    }

    public JSONObject listOrganizes(JSONObject args, JSONObject argsattributes) {
        JSONArray organizesArray = new JSONArray();

        if (!organizes.isEmpty()) {
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
                response.put("data", organizesArray);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return ResponseHelper.createResponse("Error processing arguments: " + e.getMessage(), null,RESPONSE_DEFAULT_VERSION);
            }
            JSONObject response = new JSONObject();
            response.put("organizes", organizesArray);
            return ResponseHelper.createResponse("Organizes listed successfully", response,RESPONSE_DEFAULT_VERSION);
        }

        return ResponseHelper.createResponse("No registered participant or event found.", null,RESPONSE_DEFAULT_VERSION); // Response for an empty list
    }
}
