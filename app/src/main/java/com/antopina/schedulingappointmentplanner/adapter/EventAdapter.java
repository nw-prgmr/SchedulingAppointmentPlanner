package com.antopina.schedulingappointmentplanner.adapter;

import static com.google.api.ResourceProto.resource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.Event;
import com.antopina.schedulingappointmentplanner.R;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(@NonNull Context context, List<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Event event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }
        TextView description = convertView.findViewById(R.id.description);
        TextView title = convertView.findViewById(R.id.title);

        String evenTitle = event.getName();
        title.setText(evenTitle);

        String evenDescription = event.getDescription();
        description.setText(evenDescription);

        return convertView;
    }
}
