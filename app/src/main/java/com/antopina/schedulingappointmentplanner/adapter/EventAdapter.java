package com.antopina.schedulingappointmentplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.antopina.schedulingappointmentplanner.HomePage.calendar.Event;
import com.antopina.schedulingappointmentplanner.R;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class EventAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Event> events;

    public EventAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.week_item_list, parent, false);
        }

        Event event = events.get(position);

        // Set the event name
        TextView eventTitle = convertView.findViewById(R.id.title);
        eventTitle.setText(event.getName());

        // Set the event time
        TextView eventTime = convertView.findViewById(R.id.time);
        eventTime.setText(event.getTime().toString());

        // Set the event description
        TextView eventDescription = convertView.findViewById(R.id.description);
        eventDescription.setText(event.getDescription());

        // Set the color for activityTypeTV based on event type (task or event)
        View activityTypeTV = convertView.findViewById(R.id.activityTypeTV); // Ensure this is defined in your layout XML
        if (event.isTask()) {
            // Set color for task
            activityTypeTV.setBackgroundColor(context.getResources().getColor(R.color.dark_pink));
        } else {
            // Set color for event
            activityTypeTV.setBackgroundColor(context.getResources().getColor(R.color.burgundy));
        }

        // Reference the ImageView
        ImageView donIV = convertView.findViewById(R.id.donIV);

        // Check if task status is 1 or event is past its end date/time
        if (event.getTaskStatus() == 1 || isEventPast(event)) {
            donIV.setVisibility(View.VISIBLE);  // Show the image
        } else {
            donIV.setVisibility(View.GONE);  // Hide the image
        }

        return convertView;
    }

    // Helper method to check if the event is past the end date and time
    private boolean isEventPast(Event event) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        return event.getEndDate().isBefore(currentDate) ||
                (event.getEndDate().isEqual(currentDate) && event.getEndTime().isBefore(currentTime));
    }
}
