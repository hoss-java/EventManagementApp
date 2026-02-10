public class CommandManager {
    protected final ActionCallbackInterface callback;
    protected Map<String, Method> commandMap;

    public CommandManager(ActionCallbackInterface callback) {
        this.callback = callback;
        this.commandMap = new HashMap<>();
        }


    // Check if a command is valid
    @Override
    public boolean isValidCommand(String commandId) {
        return commandMap.containsKey(commandId);
    }

    // Command parser
    @Override
    public JSONObject parseCommands(String jsonCommands) {
        JSONArray commandsArray = new JSONArray(jsonCommands);
        return parseCommands(commandsArray); // Call to second version
    }

    @Override
    public JSONObject parseCommands(JSONArray commandsArray) { // Change parameter type to JSONArray
        JSONObject response = new JSONObject();

        for (int i = 0; i < commandsArray.length(); i++) {
            JSONObject command = commandsArray.getJSONObject(i);
            String id = command.getString("id");
            JSONObject args = command.getJSONObject("args");
            JSONObject argsattributes = command.getJSONObject("argsattributes");

            if (isValidCommand(id)) {
                response = executeCommand(id, args,argsattributes);
            } else {
                response = ResponseHelper.createResponse("Unknown command: " + id, null);
            }
        }
        
        return response;
    }

    // Execute command based on command ID
    private JSONObject executeCommand(String commandId, JSONObject args,JSONObject argsattributes) {
        try {
            Method method = commandMap.get(commandId);
            if (method != null) {
                // If args is null, invoke the method with no arguments
                if (args == null) {
                    return (JSONObject) method.invoke(this);
                // If argsattributes is null, invoke the method without argsattributes
                } else if (argsattributes == null ) {
                    return (JSONObject) method.invoke(this, args);
                }else {
                    return (JSONObject) method.invoke(this, args, argsattributes);
                }
            }
        } catch (Exception e) {
            return ResponseHelper.createResponse("Error executing command: " + e.getMessage(), null);
        }
        return ResponseHelper.createResponse("Command execution failed", null);
    }

    public int findId(JSONObject args, String[][] keyTypePairs, String requestCommand) {
        return findId(args, keyTypePairs, requestCommand, "data");
    }

    public int findId(JSONObject args, String[][] keyTypePairs, String requestCommand, String entityType) {
        JSONObject response = null;

        // Iterate through provided keyTypePairs to find a matching value
        for (String[] keyTypePair : keyTypePairs) {
            if (keyTypePair.length != 2) {
                throw new IllegalArgumentException("Each keyTypePair must contain exactly two elements.");
            }

            String searchKey = keyTypePair[0];
            String searchType = keyTypePair[1];
            String searchValue = args.optString(searchKey, null);

            if (searchValue != null) {

                // Create the payload based on the current search type
                String myPayload = String.format(
                    "{\"args\":{\"%s\":\"%s\"},\"argsattributes\":{},\"id\":\"%s\"}",
                    searchType, searchValue, requestCommand
                );

                response = callback.actionHandler(getObjectId(), new JSONObject(myPayload));
                break; // Exit the loop after the first successful match
            }
        }

        // Check if the response is valid
        if (response != null) {
            response = JSONHelper.getJsonValue(response, "data");
            JSONArray entityArray = response.getJSONArray(entityType);

            // Return the id, or -1 if not found
            if (entityArray.length() > 0) {
                JSONObject item = entityArray.getJSONObject(0);
                return item.optInt("id", -1);
            }
        }

        return -1; // Return -1 if no id is found
    }
}