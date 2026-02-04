package com.EventManApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

// Custom functional interface for three arguments
@FunctionalInterface
interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}

public class ValueComparator {

    private static final Map<String, TriFunction<String, String, String, Boolean>> comparisonMethods = new HashMap<>();
    private static final Map<String, BiFunction<Integer, Integer, Boolean>> integerComparisons = new HashMap<>();
    private static final Map<String, BiFunction<Date, Date, Boolean>> dateComparisons = new HashMap<>();
    private static final Map<String, BiFunction<String, String, Boolean>> stringComparisons = new HashMap<>();

    static {
        comparisonMethods.put("str", ValueComparator::compareStrings);
        comparisonMethods.put("int", ValueComparator::compareIntegers);
        comparisonMethods.put("positiveint", ValueComparator::comparePositiveIntegers);
        comparisonMethods.put("date", ValueComparator::compareDates);
        comparisonMethods.put("time", ValueComparator::compareTimes);
        comparisonMethods.put("duration", ValueComparator::compareDurations);

        // Initialize integer comparison modes
        integerComparisons.put("=", (a, b) -> a.equals(b));
        integerComparisons.put("<", (a, b) -> a < b);
        integerComparisons.put("<=", (a, b) -> a <= b);
        integerComparisons.put(">", (a, b) -> a > b);
        integerComparisons.put(">=", (a, b) -> a >= b);

        // Initialize date comparison modes
        dateComparisons.put("=", Date::equals);
        dateComparisons.put("<", Date::before);
        dateComparisons.put("<=", (a, b) -> !a.after(b));
        dateComparisons.put(">", Date::after);
        dateComparisons.put(">=", (a, b) -> !a.before(b));

        // Initialize string comparison modes
        stringComparisons.put("=", String::equalsIgnoreCase); // Case-insensitive equality
        stringComparisons.put("!=", (a, b) -> !a.equals(b)); // Case-sensitive inequality
        stringComparisons.put("==", String::equals); // Case-sensitive equality
        stringComparisons.put("startsWith", (a, b) -> a.startsWith(b)); // Checks if one string starts with another
        stringComparisons.put("endsWith", (a, b) -> a.endsWith(b)); // Checks if one string ends with another
        stringComparisons.put("contains", (a, b) -> a.contains(b)); // Checks if one string contains another
        stringComparisons.put("length", (a, b) -> a.length() == b.length()); // Checks if two strings have the same length
        stringComparisons.put("compareTo", (a, b) -> a.compareTo(b) == 0); // Compares two strings lexicographically
        stringComparisons.put("compareToIgnoreCase", (a, b) -> a.compareToIgnoreCase(b) == 0); // Lexicographical comparison ignoring case
    }

    public static boolean validateValue(String valueStr, String compareStr, String type, String compareMode) {
        if (compareStr == null || compareStr.isEmpty()) {
            return true; // Passed if no comparison string is provided
        }

        // Get the appropriate comparison method
        TriFunction<String, String, String, Boolean> comparisonFunction = comparisonMethods.get(type.toLowerCase());
        if (comparisonFunction == null) {
            throw new IllegalArgumentException("Invalid type specified: " + type);
        }

        // Call the comparison method using the retrieved function
        return comparisonFunction.apply(valueStr, compareStr, compareMode); // pass compareMode
    }

    private static Boolean compareStrings(String valueStr, String compareStr, String compareMode) {
        BiFunction<String, String, Boolean> comparisonFunction = stringComparisons.get(compareMode);
        if (comparisonFunction != null) {
            return comparisonFunction.apply(valueStr, compareStr);
        }
        throw new IllegalArgumentException("Invalid comparison mode for string: " + compareMode);
    }

    private static Boolean compareIntegers(String valueStr, String compareStr, String compareMode) {
        try {
            int value = Integer.parseInt(valueStr);
            int compareValue = Integer.parseInt(compareStr);

            BiFunction<Integer, Integer, Boolean> comparisonFunction = integerComparisons.get(compareMode);
            if (comparisonFunction == null) {
                throw new IllegalArgumentException("Invalid comparison mode for integer: " + compareMode);
            }
            return comparisonFunction.apply(value, compareValue);
        } catch (NumberFormatException e) {
            return false; // Invalid integer format
        }
    }

    private static Boolean comparePositiveIntegers(String valueStr, String compareStr, String compareMode) {
        try {
            int value = Integer.parseInt(valueStr);
            if (value <= 0) return false; // Must be positive
            
            return compareIntegers(valueStr, compareStr, compareMode); // Pass compareMode directly
        } catch (NumberFormatException e) {
            return false; // Invalid integer format
        }
    }

    private static Boolean compareDates(String valueStr, String compareStr, String compareMode) {
        return compareTimestamps(valueStr, compareStr, compareMode, "yyyy-MM-dd");
    }

    private static Boolean compareTimes(String valueStr, String compareStr, String compareMode) {
        return compareTimestamps(valueStr, compareStr, compareMode, "HH:mm:ss");
    }

    private static Boolean compareTimestamps(String valueStr, String compareStr, String compareMode, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            Date valueDate = sdf.parse(valueStr);
            Date compareDate = sdf.parse(compareStr);

            BiFunction<Date, Date, Boolean> comparisonFunction = dateComparisons.get(compareMode);
            if (comparisonFunction == null) {
                throw new IllegalArgumentException("Invalid comparison mode for date/time: " + compareMode);
            }
            return comparisonFunction.apply(valueDate, compareDate);
        } catch (ParseException e) {
            return false; // Invalid date/time format
        }
    }

    private static Boolean compareDurations(String valueStr, String compareStr, String compareMode) {
        if ("=".equals(compareMode)) {
            return valueStr.equals(compareStr);
        }
        throw new IllegalArgumentException("Invalid comparison mode for duration: " + compareMode);
    }
}
