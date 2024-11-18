package com.antopina.schedulingappointmentplanner.HomePage.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarViewHolder;
import com.antopina.schedulingappointmentplanner.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (days.size() > 15) { // Month view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666); // Setting cell height
        } else { // Week view
            layoutParams.height = parent.getHeight();
        }
        view.setLayoutParams(layoutParams);

        return new CalendarViewHolder(view, onItemListener, days); // Pass the listener here
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        LocalDate date = days.get(position);
        if (date == null) {
            holder.dayOfMonth.setText("");
            holder.parentView.setOnClickListener(null); // Disable clicks for empty cells
            holder.parentView.setBackgroundColor(Color.TRANSPARENT); // Reset background
        } else {
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            holder.parentView.setOnClickListener(v -> {
                onItemListener.onItemClick(position, date); // Pass LocalDate directly
                System.out.println("Clicked on: " + date); // Debug click event
            });

            // Highlight selected date
            if (date.equals(CalendarUtils.selectedDate)) {
                holder.parentView.setBackgroundColor(Color.LTGRAY);
            } else {
                holder.parentView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }


    @Override
    public int getItemCount() {
        return days.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, LocalDate date);
    }
}
