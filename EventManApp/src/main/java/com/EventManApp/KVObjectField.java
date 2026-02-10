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

    public String getField() {
        return field;
    }


    public String getType() {
        return (new TokenizedString(type,"@")).getPart(-1);
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