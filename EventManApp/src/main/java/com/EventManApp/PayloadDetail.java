package com.EventManApp;

import org.json.JSONObject;

import com.EventManApp.lib.DebugUtil;
import com.EventManApp.lib.TokenizedString;

/**
 * Represents the details of a payload including various attributes 
 * such as field, type, mandatory status, default value, compare mode, 
 * and modifier.
 */
public class PayloadDetail {
    private String field;  /**< Optional, can be null */
    private String description; /**< Optional, can be null */
    private String type;  /**< Required */
    private Boolean mandatory;  /**< Optional, can be null */
    private String defaultValue; /**< Optional, can be null */
    private String compareMode; /**< Optional, can be null */
    private String modifier; /**< Optional, can be null */

    /**
     * Default constructor.
     */
    public PayloadDetail() {
    }

    /**
     * Constructor for required fields.
     * 
     * @param type the required type of the payload detail
     */
    public PayloadDetail(String type) {
        this.type = type;
    }

    /**
     * Constructor for all fields excluding field and description.
     * 
     * @param type the required type of the payload detail
     * @param mandatory indicates if this field is mandatory
     * @param defaultValue the default value of the field
     * @param compareMode the comparison mode applied to the field
     */
    public PayloadDetail(String type, Boolean mandatory, String defaultValue, String compareMode) {
        this.type = type;
        this.mandatory = mandatory; 
        this.defaultValue = defaultValue;
        this.compareMode = compareMode;
    }

    /**
     * Constructor for all fields except description.
     * 
     * @param field the name of the field
     * @param type the required type of the payload detail
     * @param mandatory indicates if this field is mandatory
     * @param defaultValue the default value of the field
     * @param compareMode the comparison mode applied to the field
     */
    public PayloadDetail(String field, String type, Boolean mandatory, String defaultValue, String compareMode) {
        this.field = field;
        this.type = type;
        this.mandatory = mandatory; 
        this.defaultValue = defaultValue;
        this.compareMode = compareMode;
    }

    /**
     * Constructor for all fields.
     * 
     * @param field the name of the field
     * @param type the required type of the payload detail
     * @param mandatory indicates if this field is mandatory
     * @param defaultValue the default value of the field
     * @param modifier the modifier applied to the field
     * @param compareMode the comparison mode applied to the field
     */
    public PayloadDetail(String field, String type, Boolean mandatory, String defaultValue, String modifier, String compareMode) {
        this.field = field;
        this.type = type;
        this.mandatory = mandatory; 
        this.defaultValue = defaultValue;
        this.compareMode = compareMode;
        this.modifier = modifier;
    }

    // Getters

    /**
     * Gets the field name.
     * 
     * @return the field name, or null if not set
     */
    public String getField() {
        return field;
    }

    /**
     * Gets the description.
     * 
     * @return the description, or null if not set
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the type as a string.
     * 
     * @return the type of the payload detail
     */
    public String getTypeChaine() {
        return type;
    }

    /**
     * Gets the last part of the type string, tokenized by the "@" character.
     * 
     * @return the last part of the type
     */
    public String getType() {
        return (new TokenizedString(type,"@")).getPart(-1);
    }

    /**
     * Determines if the field is mandatory.
     * 
     * @return true if mandatory, false if not, or null if not applicable
     */
    public Boolean isMandatory() {
        return mandatory;  // Can return null if not applicable
    }

    /**
     * Gets the default value of the field.
     * 
     * @return the default value, or null if not set
     */
    public String getDefaultValue() {
        return defaultValue;  // Can return null if not applicable
    }

    /**
     * Gets the comparison mode of the field.
     * 
     * @return the comparison mode, or null if not set
     */
    public String getCompareMode() {
        return compareMode;  // Can return null if not applicable
    }

    /**
     * Gets the modifier of the field.
     * 
     * @return the modifier, or null if not set
     */
    public String getModifier() {
        return modifier;  // Can return null if not applicable
    }

    // Optionally, include setters if mutability is desired

    /**
     * Sets the mandatory status of the field.
     * 
     * @param mandatory true if the field is mandatory, false otherwise
     */
    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    /**
     * Sets the default value of the field.
     * 
     * @param defaultValue the default value to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Sets the comparison mode of the field.
     * 
     * @param compareMode the comparison mode to set
     */
    public void setCompareMode(String compareMode) {
        this.compareMode = compareMode;
    }

    /**
     * Sets the modifier of the field.
     * 
     * @param modifier the modifier to set
     */
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    /**
     * Converts this PayloadDetail instance to a JSON object.
     * 
     * @return a JSONObject representing the PayloadDetail instance
     */
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("field", field);
        jsonObject.put("type", type);
        jsonObject.put("mandatory", mandatory);
        jsonObject.put("defaultValue", defaultValue);
        jsonObject.put("compareMode", compareMode);
        return jsonObject;
    }

    /**
     * Returns a string representation of this PayloadDetail instance in JSON format.
     * 
     * @return a JSON string representation of this instance
     */
    @Override
    public String toString() {
        return toJSON().toString();
    }
}
