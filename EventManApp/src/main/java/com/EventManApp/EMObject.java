package com.EventManApp;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.EventManApp.BaseObject;
import com.EventManApp.lib.StringParserHelper;
import com.EventManApp.lib.validators.*;

public class EMObject extends BaseObject<Object> {
    private static String objectId;
    private Map<String, EMObjectField> fieldTypeMap;

    public EMObject(String objectId, Map<String, EMObjectField> fieldTypeMap, Map<String, String> jsonFields) {
        super();
        this.objectId = objectId;
        this.fieldTypeMap = new HashMap<>();

        // Initialize the field type map
        if (fieldTypeMap != null) {
            this.fieldTypeMap.putAll(fieldTypeMap);
        }

        for (Map.Entry<String, String> entry : jsonFields.entrySet()) {
            String fieldName = entry.getKey();
            String valueStr = StringParserHelper.parseString(entry.getValue());
            EMObjectField definition = this.fieldTypeMap.get(fieldName);
            String expectedType = (definition != null) ? definition.getType() : null;


            Object value;
            if (expectedType == null) {
                // If field type does not exist, check for mandatory status
                continue; // or throw an exception based on your requirements
            } else {
                value = validateAndConvert(fieldName, valueStr, expectedType);
            }
            addField(fieldName, value);
        }

        // Handle default values for non-mandatory fields
        for (Map.Entry<String, EMObjectField> entry : this.fieldTypeMap.entrySet()) {
            String fieldName = entry.getKey();
            EMObjectField definition = entry.getValue();
            if (!jsonFields.containsKey(fieldName) && !definition.isMandatory()) {
                addField(fieldName, definition.getDefaultValue());
            }
        }
    }


    // Validator to check for duplicates in the existing collection
    public static boolean isValidForAddition(List<EMObject> existingObjects, EMObject newObject) {
        for (EMObject obj : existingObjects) {
            boolean isMatch = true;
            for (String key : newObject.getFieldNames()) { // Assuming a method exists to get field names
                // Skip comparison of the "id" field
                if ("id".equalsIgnoreCase(key)) {
                    continue;
                }
                Object valueToCompare = obj.getFieldValue(key); // Assuming a method exists to get field values
                Object currentValue = newObject.getFieldValue(key);
                if (currentValue != null && !currentValue.equals(valueToCompare)) {
                    isMatch = false;
                    break;
                }
            }
            // If a match is found, return false
            if (isMatch) {
                return false; // Duplicate found
            }
        }
        return true; // No duplicates found
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.objectId+"{");
        for (Field<Object> field : getFields()) {
            sb.append(field.getName()).append("=").append(field.getValue()).append(", ");
        }
        sb.setLength(sb.length() - 2); // Remove last comma and space
        sb.append("}");
        return sb.toString();
    }
}


