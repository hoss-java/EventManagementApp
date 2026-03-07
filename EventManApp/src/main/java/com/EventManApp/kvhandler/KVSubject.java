package com.EventManApp.kvhandler;

import org.json.JSONArray;
import org.json.JSONObject;

import com.EventManApp.kvhandler.KVBaseSubject;
import com.EventManApp.kvhandler.KVSubjectAttribute;
import com.EventManApp.kvhandler.KVObjectField;

public class KVSubject extends KVBaseSubject {
    // Constructor
    public KVSubject(KVSubjectAttribute subjectAttribute) {
        super(subjectAttribute);
    }

    // Method to convert KVSubject to JSONObject
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("attributes", subjectAttribute.toJSON());

        // Convert fieldTypeMap to JSON array
        JSONArray fieldsJsonArray = new JSONArray();
        for (KVObjectField field : fieldTypeMap.values()) {
            fieldsJsonArray.put(field.toJSON());
        }
        jsonObject.put("fieldTypeMap", fieldsJsonArray);
        
        return jsonObject;  // Return the JSON object
    }

    @Override
    public String toString() {
        return toJSON().toString(); // Use toJSON for the string representation
    }
}