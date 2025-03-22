package com.antopina.schedulingappointmentplanner.HomePage.calendar;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.antopina.schedulingappointmentplanner.adapter.CalendarAdapter;
import com.antopina.schedulingappointmentplanner.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ArrayList<LocalDate> days;
    public final View parentView;
    public final TextView dayOfMonth;
    public final LinearLayout eventsContainer;  // Declare eventsListView
    private final CalendarAdapter.OnItemListener onItemListener;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<LocalDate> days) {
        super(itemView);
        parentView = itemView.findViewById(R.id.parentView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        eventsContainer = itemView.findViewById(R.id.eventsContainer); // Initialize ListView
        this.onItemListener = onItemListener; // Initialize the listener
        itemView.setOnClickListener(this); // Set click listener
        this.days = days;
    }

    @Override
    public void onClick(View view) {
        if (onItemListener != null) {
            onItemListener.onItemClick(getAdapterPosition(), days.get(getAdapterPosition()));
        }
    }
}
