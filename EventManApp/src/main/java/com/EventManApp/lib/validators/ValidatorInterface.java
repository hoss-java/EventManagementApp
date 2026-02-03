package com.EventManApp.lib.validators;

public interface ValidatorInterface<T> {
    boolean isValid(T value);
    String getErrorMessage();
}
