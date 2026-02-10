package com.EventManApp;

import com.EventManApp.KVBaseSubject;

public class KVSubject extends KVBaseSubject {

    // Constructor
    public KVSubject(String identifier) {
        super(identifier);
        // No need to call initializeFieldTypeMap(); it will be done in the handler
    }
}
