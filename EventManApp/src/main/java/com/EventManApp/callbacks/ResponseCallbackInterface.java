package com.EventManApp.callbacks;

@FunctionalInterface
public interface ResponseCallbackInterface {
    String ResponseHandler(String callerID, String menuItem);
}