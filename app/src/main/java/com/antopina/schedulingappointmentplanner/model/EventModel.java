package com.antopina.schedulingappointmentplanner.model;

public class EventModel {
    private long id; // Change from String to long
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;

    public EventModel(long id, String title, String description, String startDate, String endDate, String startTime, String endTime) {
        this.id = id;
        this.title = title;
        this.description = description != null ? description : "No description";  // Set "No description" if null
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getId() {
        return id; // Return long
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}

