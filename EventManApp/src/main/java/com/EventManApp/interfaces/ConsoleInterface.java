package com.EventManApp.interfaces;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.EventManApp.BaseInterface;
import com.EventManApp.ResponseCallbackInterface;
import com.EventManApp.lib.DebugUtil;
import com.EventManApp.MenuUI;
import com.EventManApp.InputUI;

public class ConsoleInterface extends BaseInterface {
    private final Scanner scanner;

    public ConsoleInterface(ResponseCallbackInterface callback) {
        super(callback);
        this.scanner = new Scanner(System.in);
    }

    private void runSelectedCommand(JSONObject selectedMenuObject){
        InputUI inputUI = new InputUI();

        JSONObject peyload = new JSONObject();

        peyload.put("identifier",selectedMenuObject.getString("identifier") ); 

        JSONObject command = selectedMenuObject.getJSONObject("command");
        if (command.has("args")) {
            JSONObject args = new JSONObject();
            JSONObject arguments = command.getJSONObject("args");

            for (String argName : arguments.keySet()) {
                JSONObject argType = arguments.getJSONObject(argName);
                String argValue = inputUI.getUserInput(argName, argType);
                String argField = argType.optString("field", argName);
                args.put(argField, argValue);
            }
            JSONObject peyloadCommand = new JSONObject();
            peyloadCommand.put("id", command.getString("action"));
            peyloadCommand.put("data", args);
            peyloadCommand.put("args", arguments);

            JSONArray payloadCommandsArray = new JSONArray();
            payloadCommandsArray.put(peyloadCommand);
            // Put the commands list into the JSON object
            peyload.put("commands", payloadCommandsArray);                
        }

        String response = callback.ResponseHandler("ConsoleInterface",peyload.toString());
        //System.out.println("Response: " + response); // Print the JSON response
        printJson(response);
        inputUI.waitForKeyPress();

    }

    @Override
    public JSONObject executeCommands(JSONObject commands) {
        MenuUI menuUI = new MenuUI("Available Commands (ConsoleInterface)");
        while (true) {
            JSONObject selectedMenuObject = menuUI.displayMenu(commands);
            if (selectedMenuObject.isEmpty() ){
                return selectedMenuObject.put("ConsoleInterface", "exit");
            }
            runSelectedCommand(selectedMenuObject);
        }        
    }

}
