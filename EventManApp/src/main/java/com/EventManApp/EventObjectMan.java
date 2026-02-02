package com.EventManApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.EventManApp.EventObject;
import com.EventManApp.ObjectHandler;

public class EventObjectMan implements ObjectHandler {
    private List<EventObject> events;
    private int nextId; // Counter for generating unique IDs
    private Map<String, Method> commandMap;

    public EventObjectMan() {
        this.events = new ArrayList<>();
        this.nextId = 1; // Start ID from 1

        this.commandMap = new HashMap<>();
        initializeCommands();
    }

    // Method to initialize commands and their associated methods
    private void initializeCommands() {
        try {
            commandMap.put("addevent", this.getClass().getDeclaredMethod("addEventFromArgs",JSONObject.class));
            commandMap.put("listallupcomingevents", this.getClass().getDeclaredMethod("listEvents",JSONObject.class)); 
            commandMap.put("listallevents", this.getClass().getDeclaredMethod("listEvents",JSONObject.class)); 
            // Add more commands here
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    // Check if a command is valid
    @Override
    public boolean isValidCommand(String commandId) {
        return commandMap.containsKey(commandId);
    }

    // Add an event
    public JSONObject addEvent(EventObject event) {
        events.add(event);
        return createResponse("Event added successfully", null);
    }

    // Remove an event by title
    public JSONObject removeEvent(String title) {
        if (events.removeIf(event -> event.getTitle().equals(title))) {
            return createResponse("Event removed successfully", null);
        } else {
            return createResponse("Event not found", null);
        }
    }

    public JSONObject listEvents(JSONObject args) {
        JSONArray eventsArray = new JSONArray();

        if (events.isEmpty()) {
            return createResponse("No events found.", null); // Response for an empty list
        }

        // Declare Date and Time formatters outside the loop for accessibility
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (EventObject event : events) {
            // Check for filters in the args JSON object
            boolean matches = true;

            if (args.has("title")) {
                String titleFilter = args.getString("title");
                if (!event.getTitle().contains(titleFilter)) {
                    matches = false;
                }
            }

            if (args.has("date")) {
                String dateFilter = args.getString("date");
                try {
                    LocalDate filterDate = LocalDate.parse(dateFilter, dateFormatter);
                    if (event.getDate() == null || event.getDate().isBefore(filterDate)) {
                        matches = false;
                    }
                } catch (DateTimeParseException e) {
                    matches = false; // Invalid date format
                }
            }

            if (args.has("startTime")) {
                String startTimeFilter = args.getString("startTime");
                try {
                    LocalTime filterTime = LocalTime.parse(startTimeFilter, timeFormatter);
                    if (event.getStartTime() == null || event.getStartTime().isBefore(filterTime)) {
                        matches = false;
                    }
                } catch (DateTimeParseException e) {
                    matches = false; // Invalid time format
                }
            }

            if (args.has("duration")) {
                int durationFilter = args.getInt("duration");
                if (event.getDuration().toMinutes() < durationFilter) {
                    matches = false;
                }
            }

            if (args.has("capacity")) {
                int capacityFilter = args.getInt("capacity");
                if (event.getCapacity() < capacityFilter) {
                    matches = false;
                }
            }

            if (matches) {
                JSONObject eventJson = new JSONObject();
                eventJson.put("uniqueId", event.getUniqueId());
                eventJson.put("title", event.getTitle());
                eventJson.put("date", event.getDate().toString());
                eventJson.put("startTime", event.getStartTime().toString());
                eventJson.put("duration", event.getDuration().toMinutes());
                eventJson.put("location", event.getLocation());
                eventJson.put("capacity", event.getCapacity());
                eventsArray.put(eventJson);
            }
        }

        JSONObject response = new JSONObject();
        response.put("events", eventsArray);
        return createResponse("Events listed successfully", response);
    }

    // Command parser
    @Override
    public JSONObject parseCommands(String jsonCommands) {
        JSONArray commandsArray = new JSONArray(jsonCommands);
        JSONObject response = new JSONObject();

        for (int i = 0; i < commandsArray.length(); i++) {
            JSONObject command = commandsArray.getJSONObject(i);
            String id = command.getString("id");
            JSONObject args = command.getJSONObject("args");

            if (isValidCommand(id)) {
                response = executeCommand(id, args);
            } else {
                response = createResponse("Unknown command: " + id, null);
            }
        }
        
        return response;
    }

    // Execute command based on command ID
    private JSONObject executeCommand(String commandId, JSONObject args) {
        try {
            Method method = commandMap.get(commandId);
            if (method != null) {
                // If args is null, invoke the method with no arguments
                if (args == null) {
                    return (JSONObject) method.invoke(this);
                } else {
                    return (JSONObject) method.invoke(this, args);
                }
            }
        } catch (Exception e) {
            return createResponse("Error executing command: " + e.getMessage(), null);
        }
        return createResponse("Command execution failed", null);
    }

    // Helper method to create an event from args
    private JSONObject addEventFromArgs(JSONObject args) {
        try {
            // Validate required parameters
            String title = args.optString("title", null);
            LocalDate date = args.optString("date", null) != null ? LocalDate.parse(args.getString("date")) : null;
            LocalTime startTime = args.optString("time", null) != null ? LocalTime.parse(args.getString("time")) : null;
            String durationStr = args.optString("duration", null); // Get duration as a string
            Duration duration = parseDuration(durationStr); // Parse the duration string
            String location = args.optString("location", null);
            int capacity = args.optInt("capacity", 0);

            // Check if all parameters are valid
            if (title == null || date == null || startTime == null || duration == null || location == null || capacity <= 2) {
                return createResponse("Invalid parameters", null);
            }

            // Create the event object with a unique ID
            EventObject event = new EventObject(nextId++, title, date, startTime, duration, location, capacity);
            events.add(event); // Add the event to the list
            return createResponse("Event added successfully", new JSONObject().put("uniqueId", event.getUniqueId()));

        } catch (Exception e) {
            return createResponse("Error parsing command arguments: " + e.getMessage(), null);
        }
    }

    private Duration parseDuration(String durationStr) {
        int hours = 0;
        int minutes = 0;

        // Regex to extract hours and minutes
        Pattern pattern = Pattern.compile("(\\d+)h\\s*(\\d+)m|(?:(\\d+)h)|(\\d+)m");
        Matcher matcher = pattern.matcher(durationStr);

        if (matcher.find()) {
            if (matcher.group(1) != null) {
                hours = Integer.parseInt(matcher.group(1));
            }
            if (matcher.group(2) != null) {
                minutes = Integer.parseInt(matcher.group(2));
            } else if (matcher.group(3) != null) {
                hours = Integer.parseInt(matcher.group(3));
            } else if (matcher.group(4) != null) {
                minutes = Integer.parseInt(matcher.group(4));
            }
        } else {
            throw new IllegalArgumentException("Invalid duration format. Example: '1h 30m'");
        }

        return Duration.ofHours(hours).plusMinutes(minutes);
    }

    // Helper method for creating JSON responses
    private JSONObject createResponse(String message, JSONObject data) {
        JSONObject response = new JSONObject();
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        return response;
    }
}
