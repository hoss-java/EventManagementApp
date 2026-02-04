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
            commandMap.put("listallorganizes", this.getClass().getDeclaredMethod("listOrganizes",JSONObject.class,JSONObject.class)); 
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

    // Remove a registration by Organize name
    public JSONObject removeOrganize(int id) {
//        if (organizes.removeIf(organize -> ((String)  organize.getFieldValue("name")).equals(name))) {
//            return ResponseHelper.createResponse("Organize removed successfully", null);
//        } else {
//            return ResponseHelper.createResponse("Organize not found", null);
//        }
        return ResponseHelper.createResponse("Organize not found", null);
    }

    public int findEventId(JSONObject args) {
        JSONObject response = null;
        int eventtid = -1;

        String eventStr = args.optString("eventid", null);
        if ( eventStr == null){
            eventStr = args.optString("eventtitle", null);
            if (eventStr == null){
                return eventtid;
            }
            String myPayload = "{\"args\":{\"title\":\""+eventStr+"\"},\"argsattributes\":{},\"id\":\"listallevents\"}";
            response = callback.actionHandler(getObjectId(), new JSONObject(myPayload));
        }
        else{
            String myPayload = "{\"args\":{\"id\":\""+eventStr+"\"},\"argsattributes\":{},\"id\":\"listallevents\"}";
            response = callback.actionHandler(getObjectId(), new JSONObject(myPayload));
        }
        if (response != null ){
            response = JSONHelper.getJsonValue(response,"data");
            System.out.println(response.toString());
            JSONArray eventsArray = response.getJSONArray("events");
            if (eventsArray.length() > 0){
                JSONObject event = eventsArray.getJSONObject(0);
                eventtid = event.optInt("id",-1);
            }
        }
        return eventtid;
    }

    public int findparticipantId(JSONObject args) {
        JSONObject response = null;
        int participantid = -1;

        String participantStr = args.optString("participantid", null);
        if ( participantStr == null){
            participantStr = args.optString("participantname", null);
            if (participantStr == null){
                return -participantid;
            }
            String myPayload = "{\"args\":{\"name\":\""+participantStr+"\"},\"argsattributes\":{},\"id\":\"listallparticipants\"}";
            response = callback.actionHandler(getObjectId(), new JSONObject(myPayload));
        }
        else{
            String myPayload = "{\"args\":{\"id\":\""+participantStr+"\"},\"argsattributes\":{},\"id\":\"listallparticipants\"}";
            response = callback.actionHandler(getObjectId(), new JSONObject(myPayload));
        }

        if (response != null ){
            response = JSONHelper.getJsonValue(response,"data");
            System.out.println(response.toString());
            JSONArray participantsArray = response.getJSONArray("events");
            if (participantsArray.length() > 0){
                JSONObject participant = participantsArray.getJSONObject(0);
                participantid = participant.optInt("id",-1);
            }
        }
        return participantid;
    }

    // Helper method to create an event from args
    public JSONObject addOrganizeFromArgs() {
        return addOrganizeFromArgs(null, null);
    }

    public JSONObject addOrganizeFromArgs(JSONObject args) {
        return addOrganizeFromArgs(args, null);
    }

    public JSONObject addOrganizeFromArgs(JSONObject args, JSONObject argsattributes) {
        if (args == null){
            return ResponseHelper.createResponse("Error: No arguments were provided!", null);
        }

        return ResponseHelper.createResponse("Organize processing arguments: " + Integer.toString(findEventId(args))+" , " + Integer.toString(findparticipantId(args)) , null);
            

//        try {
//            // Initialize a map to store participant fields
//            Map<String, String> organizeFields = new HashMap<>();
//
//            // Loop through fieldTypeMap to validate and populate organizeFields
//            for (Map.Entry<String, EMObjectField> entry : fieldTypeMap.entrySet()) {
//                String fieldName = entry.getKey();
//                EMObjectField definition = entry.getValue();
//                String value;
//                
//                if (definition.getModifier().equals("auto")){
//                    value = Integer.toString(this.nextId);
//                    this.nextId++;
//                }
//                else{
//                    value = args.optString(fieldName, null);
//                }
//                // Check for mandatory fields
//                if (definition.isMandatory() && value == null) {
//                    return ResponseHelper.createResponse("Error: " + fieldName + " is a mandatory field.", null);
//                }
//
//                // Use default value if the field is not mandatory and is missing
//                if (value == null && !definition.isMandatory() && !definition.getModifier().equals("auto")) {
//                    value = definition.getDefaultValue();
//                }
//
//                // Add value to organizeFields
//                organizeFields.put(fieldName, value);
//            }
//
//            // Create the EMObject
//            EMObject organize = new EMObject(emObjectId, fieldTypeMap, organizeFields);
//            addOrganize(organize); // Add the organize to the list
//
//            return ResponseHelper.createResponse("Participant registered successfully", new JSONObject().put("uniqueId", (int) organize.getFieldValue("id")));
//
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return ResponseHelper.createResponse("Organize processing arguments: " + e.getMessage(), null);
//        }
    }

    public JSONObject listOrganizes() {
        return listOrganizes(null, null);
    }

    public JSONObject listOrganizes(JSONObject args) {
        return listOrganizes(args, null);
    }

    public JSONObject listOrganizes(JSONObject args, JSONObject argsattributes) {
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
