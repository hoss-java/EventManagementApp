package com.EventManApp;

public class EMObjectField {
    private String type;
    private boolean mandatory;
    private String modifier;
    private String defaultValue;

    public EMObjectField(String type, boolean mandatory, String modifier, String defaultValue) {
        this.type = type;
        this.mandatory = mandatory;
        this.modifier = modifier;
        this.defaultValue = defaultValue;
    }

    public String getType() {
        return type;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public String getModifier() {
        return modifier;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
}