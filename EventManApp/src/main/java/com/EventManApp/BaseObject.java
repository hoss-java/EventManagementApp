package com.EventManApp;

import java.util.ArrayList;
import java.util.List;

import com.EventManApp.lib.validators.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseObject<T> {
    protected List<Field<T>> fields;
    private static final Map<String, String> FIELD_TYPE_MAP = new HashMap<>();

    public BaseObject() {
        fields = new ArrayList<>();
    }

    protected void addField(String name, T value) {
        fields.add(new Field<>(name, value));
    }

    public List<Field<T>> getFields() {
        return fields;
    }

    public T getFieldValue(String fieldName) {
        for (Field<T> field : getFields()) {
            if (field.getName().equals(fieldName)) {
                return field.getValue();
            }
        }
        throw new IllegalArgumentException("Field not found: " + fieldName);
    }

    protected Object validateAndConvert(String fieldName, String valueStr, String expectedType) {
        switch (expectedType) {
            case "str":
                return valueStr;
            case "int":
                int intValue = Integer.parseInt(valueStr);
                return intValue;
            case "positiveInt":
                int positiveIntValue = Integer.parseInt(valueStr);
                if (positiveIntValue <= 0) {
                    throw new IllegalArgumentException(fieldName + " must be a positive integer.");
                }
                return positiveIntValue;
            case "date":
                return java.time.LocalDate.parse(valueStr);
            case "time":
                return java.time.LocalTime.parse(valueStr);
            case "duration":
                return java.time.Duration.parse(valueStr);
            default:
                throw new IllegalArgumentException("Unknown type for field: " + fieldName);
        }
    }

    public static class Field<T> {
        private String name;
        private T value;

        public Field(String name, T value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public T getValue() {
            return value;
        }
    }
}

