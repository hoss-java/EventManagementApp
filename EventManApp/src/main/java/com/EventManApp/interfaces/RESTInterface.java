package com.EventManApp.interfaces;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.io.InputStream;
import java.util.Properties;
import java.nio.charset.StandardCharsets;

import com.EventManApp.BaseInterface;
import com.EventManApp.ResponseCallbackInterface;
import com.EventManApp.lib.DebugUtil;
import com.EventManApp.lib.TokenizedString;

public class RESTInterface extends BaseInterface {
    private HttpServer server;
    private String configFile = "restinterface.properties";
    private int servicePort = 4567;
    private String servicePath = "api";
    private String serviceApiPath = "get";
    private String serviceCmdPath = "cmd";
    private JSONObject commands;

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
            this.servicePort = Integer.parseInt(props.getProperty("rest.port"));
            this.servicePath = props.getProperty("rest.path");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RESTInterface(ResponseCallbackInterface callback) {
        super(callback);
        loadProperties();
    }

    @Override
    public JSONObject executeCommands(JSONObject commands) {
        this.commands = commands;
        start(); // Start the server upon instantiation

        try {
            // Keep the service running while the running flag is true
            while (getRunningFlag()) {
                Thread.sleep(100); // Short sleep to yield control
            }
        } catch (InterruptedException e) {
            // Log interruption and reset the interrupt status
            Thread.currentThread().interrupt();
            System.out.println("Service thread was interrupted.");
        } finally {
            shutDown(); // Call shutdown method before exiting
        }
        return new JSONObject().put("status", "stopped");
    }

    private void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(this.servicePort), 0);

            // Define your endpoints
            server.createContext("/"+this.servicePath+"/"+serviceApiPath, this::handleGetAPI);
            server.createContext("/"+this.servicePath+"/"+serviceCmdPath, this::handleCommand);

            server.start(); // Start the server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleGetAPI(HttpExchange exchange) throws IOException {
        // Read the request body sent by the client
        InputStream inputStream = exchange.getRequestBody();
        String payload = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8); // Convert input stream to string

        // Set response headers and send response
        String response = commands.toString();
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8)); // Send the response back to the client
        }
    }

    private void handleCommand(HttpExchange exchange) throws IOException {
        // Read the request body sent by the client
        InputStream inputStream = exchange.getRequestBody();
        String payload = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8); // Convert input stream to string

        // Process the payload using your callback
        String response = callback.ResponseHandler("RESTInterface", payload);

        // Set response headers and send response
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8)); // Send the response back to the client
        }
    }

    private void shutDown() {
        if (server != null) {
            server.stop(0); // Stop the HTTP server immediately
        }
    }

}
