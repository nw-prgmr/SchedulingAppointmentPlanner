package com.antopina.schedulingappointmentplanner.model;

public class WeekItem {
    private String title;
    private String time;
    private String description;
    private boolean isActivity; // true for activity, false for task

    public WeekItem(String title, String time, String description, boolean isActivity) {
        this.title = title;
        this.time = time;
        this.description = description;
        this.isActivity = isActivity;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActivity() {
        return isActivity;
    }
}

