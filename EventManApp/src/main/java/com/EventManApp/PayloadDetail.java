package com.EventManApp;

import org.json.JSONObject;

import com.EventManApp.lib.DebugUtil;
import com.EventManApp.lib.TokenizedString;

public class PayloadDetail {
    private String field;  // Optional, can be null
    private String description; // Optional, can be null
    private String type;  // Required
    private Boolean mandatory;  // Optional, can be null
    private String defaultValue; // Optional, can be null
    private String compareMode; // Optional, can be null
    private String modifier; // Optional, can be null

    // Default constructor
    public PayloadDetail() {
    }

    // Constructor for required fields
    public PayloadDetail(String type) {
        this.type = type;
    }

    // Constructor for all fields
    public PayloadDetail(String type, Boolean mandatory, String defaultValue, String compareMode) {
        this.type = type;
        this.mandatory = mandatory; 
        this.defaultValue = defaultValue;
        this.compareMode = compareMode;
    }

    // Constructor for all fields
    public PayloadDetail(String field, String type, Boolean mandatory, String defaultValue, String compareMode) {
        this.field = field;
        this.type = type;
        this.mandatory = mandatory; 
        this.defaultValue = defaultValue;
        this.compareMode = compareMode;
    }

    // Constructor for all fields
    public PayloadDetail(String field, String type, Boolean mandatory, String defaultValue, String modifier, String compareMode) {
        this.field = field;
        this.type = type;
        this.mandatory = mandatory; 
        this.defaultValue = defaultValue;
        this.compareMode = compareMode;
        this.modifier = modifier;
    }

    // Getters
    public String getField() {
        return field;
    }

    public String getDescription() {
        return description;
    }

    public String getTypeChaine() {
        return type;
    }

    public String getType() {
        return (new TokenizedString(type,"@")).getPart(-1);
    }

    public Boolean isMandatory() {
        return mandatory;  // Can return null if not applicable
    }

    public String getDefaultValue() {
        return defaultValue;  // Can return null if not applicable
    }

    public String getCompareMode() {
        return compareMode;  // Can return null if not applicable
    }

    public String getModifier() {
        return modifier;  // Can return null if not applicable
    }

    // Optionally, include setters if mutability is desired
    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setCompareMode(String compareMode) {
        this.compareMode = compareMode;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("field", field);
        jsonObject.put("type", type);
        jsonObject.put("mandatory", mandatory);
        jsonObject.put("defaultValue", defaultValue);
        jsonObject.put("compareMode", compareMode);
        return jsonObject;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}

