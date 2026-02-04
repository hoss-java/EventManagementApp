package com.EventManApp;

@FunctionalInterface
public interface ResponseCallbackInterface {
    String ResponseHandler(String callerID, String menuItem);
}