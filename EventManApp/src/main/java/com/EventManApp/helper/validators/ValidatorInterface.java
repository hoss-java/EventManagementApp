package com.EventManApp.helper.validators;

public interface ValidatorInterface<T> {
    boolean isValid(T value);
    String getErrorMessage();
}
