package com.EventManApp;

import org.json.JSONObject;
import com.EventManApp.lib.DebugUtil;
import com.EventManApp.lib.TokenizedString;

public class KVObjectField {
    private String field;
    private String type;
    private boolean mandatory;
    private String modifier;
    private String defaultValue;

    public KVObjectField(String field, String type, boolean mandatory, String modifier, String defaultValue) {
        this.field = field;
        this.type = type;
        this.mandatory = mandatory;
        this.modifier = modifier;
        this.defaultValue = defaultValue;
    }

    public String getFullField() {
        return field;
    }

    public String getField() {
        return (new TokenizedString(field,"@")).getPart(0);
    }

    public String getEnterField() {
        return (new TokenizedString(field,"@")).getPart(-1);
    }

    public String getFullType() {
        return type;
    }

    public String getType() {
        return (new TokenizedString(type,"@")).getPart(0);
    }

    public String getEnterType() {
        return (new TokenizedString(type,"@")).getPart(-1);
    }

    // New method to get the SQL equivalent of the type
    public String getSqlType() {
        switch (getType().toLowerCase()) {
            case "str":
                return "TEXT"; // Maps to SQL TEXT
            case "int":
                return "INTEGER"; // Maps to SQL INTEGER
            case "unsigned":
                return "INTEGER"; // Maps to SQL INTEGER, enforce unsigned in application logic
            case "date":
                return "DATE"; // Maps to SQL DATE
            case "time":
                return "TIME"; // Maps to SQL TIME
            case "duration":
                return "VARCHAR(255)"; // Maps to SQL VARCHAR for duration, adjust as needed
            default:
                System.out.println("Hint: Unknown argument type: " + type);
                throw new IllegalArgumentException("Unknown type: " + type);
        }
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

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("field", this.field);
        jsonObject.put("type", this.type);
        jsonObject.put("mandatory", this.mandatory);
        jsonObject.put("modifier", this.modifier);
        jsonObject.put("defaultValue", this.defaultValue);
        return jsonObject;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}