package com.EventManApp.interfaces;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

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

    public ConsoleInterface(ResponseCallbackInterface callback, PrintStream out, InputStream in) {
        super(callback,out,in);
        this.scanner = new Scanner(this.in);
    }

    public ConsoleInterface(ResponseCallbackInterface callback) {
        this(callback, System.out, System.in);
    }

    private void runSelectedCommand(JSONObject selectedMenuObject) {
        InputUI inputUI = new InputUI(this.out, this.in);

        JSONObject payload = new JSONObject();

        payload.put("identifier", selectedMenuObject.getString("identifier"));

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
            JSONObject payloadCommand = new JSONObject();
            payloadCommand.put("id", command.getString("action"));
            payloadCommand.put("data", args);
            payloadCommand.put("args", arguments);

            JSONArray payloadCommandsArray = new JSONArray();
            payloadCommandsArray.put(payloadCommand);
            // Put the commands list into the JSON object
            payload.put("commands", payloadCommandsArray);
        }

        String response = callback.ResponseHandler("ConsoleInterface", payload.toString());
        printJson(response);
        inputUI.waitForKeyPress();
    }

    @Override
    public JSONObject executeCommands(JSONObject commands) {
        MenuUI menuUI = new MenuUI("Available Commands (ConsoleInterface)", this.out, this.in);
        while (true) {
            JSONObject selectedMenuObject = menuUI.displayMenu(commands);
            if (selectedMenuObject.isEmpty()) {
                return selectedMenuObject.put("ConsoleInterface", "exit");
            }
            runSelectedCommand(selectedMenuObject);
        }
    }
}
