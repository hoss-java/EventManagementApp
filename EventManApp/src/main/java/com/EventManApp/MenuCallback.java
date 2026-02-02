package com.EventManApp;

@FunctionalInterface
public interface MenuCallback {
    String onMenuItemSelected(String callerID, String menuItem);
}
