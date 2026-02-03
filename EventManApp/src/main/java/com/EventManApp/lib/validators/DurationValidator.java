package com.EventManApp.lib.validators;

import java.time.Duration;

import com.EventManApp.lib.validators.ValidatorInterface;

public class DurationValidator implements ValidatorInterface<Duration> {
    @Override
    public boolean isValid(Duration value) {
        return value != null && !value.isNegative(); // Example: No negative durations
    }

    @Override
    public String getErrorMessage() {
        return "Invalid duration: It must not be negative.";
    }
}
