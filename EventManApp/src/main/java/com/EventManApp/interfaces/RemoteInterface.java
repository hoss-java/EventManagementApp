package com.EventManApp.interfaces;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Properties;

import com.EventManApp.BaseInterface;
import com.EventManApp.ResponseCallbackInterface;
import com.EventManApp.lib.DebugUtil;
import com.EventManApp.lib.TokenizedString;
import com.EventManApp.lib.RestServiceUtil;
import com.EventManApp.MenuUI;
import com.EventManApp.InputUI;

public class RemoteInterface extends BaseInterface {
    private final Scanner scanner;
    private String configFile = "remoteinterface.properties";
    private String serviceAddress = "172.32.0.11";
    private int servicePort = 32768;
    private String servicePath = "api";
    private String serviceUrl;
    private String serviceApiPath = "get";
    private String serviceCmdPath = "cmd";

    private void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                System.out.println("Sorry, unable to find "+ configFile);
                return;
            }

            // Load properties file
            props.load(input);

            // Get the properties
            this.servicePort = Integer.parseInt(props.getProperty("remoterest.port"));
            this.servicePath = props.getProperty("remoterest.path");
            this.serviceAddress = props.getProperty("remoterest.address");
            this.serviceUrl = "http://"+this.serviceAddress+":"+this.servicePort+"/"+this.servicePath;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RemoteInterface(ResponseCallbackInterface callback) {
        super(callback);
        this.scanner = new Scanner(System.in);
        loadProperties();
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

        String response = RestServiceUtil.callRestService(this.serviceUrl+"/"+this.serviceCmdPath, peyload.toString());
        //System.out.println("Response: " + response); // Print the JSON response
        printJson(response);
        inputUI.waitForKeyPress();

    }

    @Override
    public JSONObject executeCommands(JSONObject commands) {
        if (RestServiceUtil.isServiceAvailable(this.serviceUrl+"/"+this.serviceApiPath)) {
            //ignore commands passed by server and try to get command from the REST service
            String response = RestServiceUtil.callRestService(this.serviceUrl+"/"+this.serviceApiPath, null);
            JSONObject commandsJSONFromRemote = new JSONObject(response);

            MenuUI menuUI = new MenuUI("Available Commands (RemoteInterface)");
            while (true) {
                JSONObject selectedMenuObject = menuUI.displayMenu(commandsJSONFromRemote);
                if (selectedMenuObject.isEmpty() ){
                    return selectedMenuObject.put("RemoteInterface", "exit");
                }
                runSelectedCommand(selectedMenuObject);
            }
        }
        else {
            System.out.println("Service is not available "+this.serviceUrl);
            return null;
        }
    }
}
