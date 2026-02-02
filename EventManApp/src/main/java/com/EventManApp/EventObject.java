package com.EventManApp;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventObject {
    private int uniqueId; // Unique identifier for the event
    private String title;
    private LocalDate date;
    private LocalTime startTime;
    private Duration duration;
    private String location;
    private int capacity;

    // Updated constructor to include uniqueId
    public EventObject(int uniqueId, String title, LocalDate date, LocalTime startTime, Duration duration, String location, int capacity) {
        if (uniqueId <= 0) {
            throw new IllegalArgumentException("Unique ID must be a positive integer.");
        }
        this.uniqueId = uniqueId;
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.location = location;
        this.capacity = capacity;
    }

    // Getters
    public int getUniqueId() {
        return uniqueId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "EventObject{" +
                "uniqueId=" + uniqueId +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", startTime=" + startTime +
                ", duration=" + duration.toMinutes() + " minutes" +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}
