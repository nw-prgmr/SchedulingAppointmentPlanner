package com.antopina.schedulingappointmentplanner.HomePage.calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event {

    // Static list to hold events (can be changed based on your architecture)
    public static ArrayList<Event> eventsList = new ArrayList<>();

    // Fields for event data
    private String name;
    private String description;
    private LocalDate date;          // Event date
    private LocalTime time;          // Event time
    private boolean isTask;          // To differentiate between task and event
    private int taskStatus;          // Task status (e.g., 0 for pending, 1 for completed)
    private LocalDate endDate;       // End date for the event (for duration-based events)
    private LocalTime endTime;       // End time for the event

    // Constructor to initialize event properties
    public Event(String name, String description, LocalDate date, LocalTime time, boolean isTask, int taskStatus, LocalDate endDate, LocalTime endTime) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.isTask = isTask;
        this.taskStatus = taskStatus;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    // Getters and Setters for each field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public boolean isTask() {
        return isTask;
    }

    public void setTask(boolean isTask) {
        this.isTask = isTask;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    // Override toString method for debugging/logging purposes
    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", isTask=" + isTask +
                ", taskStatus=" + taskStatus +
                ", endDate=" + endDate +
                ", endTime=" + endTime +
                '}';
    }

    // Static method to fetch events for a specific date
    public static ArrayList<Event> eventsForDate(LocalDate date) {
        ArrayList<Event> events = new ArrayList<>();
        for (Event event : eventsList) {
            if (event.getDate().equals(date)) {
                events.add(event);
            }
        }
        return events;
    }

    // Static method to fetch events for a specific date and time (e.g., for a particular hour)
    public static ArrayList<Event> eventsForDateAndTime(LocalDate date, LocalTime time) {
        ArrayList<Event> events = new ArrayList<>();
        for (Event event : eventsList) {
            if (event.getDate().equals(date) && event.getTime().getHour() == time.getHour()) {
                events.add(event);
            }
        }
        return events;
    }

    // Helper method to check if the event has passed based on the current date/time
    public boolean isPastEvent() {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Check if the event's end date and time is in the past
        return (endDate.isBefore(currentDate) || (endDate.isEqual(currentDate) && endTime.isBefore(currentTime)));
    }

    // Optional: Static helper to find events by taskStatus (e.g., completed or pending tasks)
    public static ArrayList<Event> getEventsByStatus(int status) {
        ArrayList<Event> filteredEvents = new ArrayList<>();
        for (Event event : eventsList) {
            if (event.getTaskStatus() == status) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }
}
