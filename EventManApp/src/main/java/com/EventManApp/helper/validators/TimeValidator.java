package com.EventManApp.helper.validators;

import java.time.LocalTime;

import com.EventManApp.helper.validators.ValidatorInterface;

public class TimeValidator implements ValidatorInterface<LocalTime> {
    @Override
    public boolean isValid(LocalTime value) {
        return value != null; // Example: Just ensure it's not null
    }

    @Override
    public String getErrorMessage() {
        return "Invalid time: Must not be null.";
    }
}
