package com.EventManApp;

import org.json.JSONObject;

@FunctionalInterface
public interface ActionCallbackInterface {
    JSONObject actionHandler(String callerID, JSONObject payload);
}