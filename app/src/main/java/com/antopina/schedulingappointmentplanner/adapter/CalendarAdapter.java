package com.antopina.schedulingappointmentplanner.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarViewHolder;
import com.antopina.schedulingappointmentplanner.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;

    private final Context context;

    //color
    private int blackColor;
    private int bgColor;

    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener, Context context) {
        this.days = days;
        this.onItemListener = onItemListener;
        this.context = context;

        // Initialize colors using ContextCompat and the provided context
        this.blackColor = ContextCompat.getColor(context, R.color.black);
        this.bgColor = ContextCompat.getColor(context, R.color.light_pink);
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
        LocalDate today = LocalDate.now(); // Get today's date

        // Set the text for the day
        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));

        // Set the click listener
        holder.parentView.setOnClickListener(v -> {
            CalendarUtils.selectedDate = date; // Update the selected date
            onItemListener.onItemClick(position, date); // Notify listener
            notifyDataSetChanged(); // Refresh the view to apply changes
        });

        // Highlight the selected date
        if (date.equals(CalendarUtils.selectedDate)) {
            holder.parentView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_pink));
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        }
        // Highlight today's date
        else if (date.equals(today)) {
            holder.parentView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.bgcolor));
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
        }
        // Handle dates in the current month
        else if (date.getMonth().equals(CalendarUtils.selectedDate.getMonth())) {
            holder.parentView.setBackgroundColor(Color.TRANSPARENT); // Default background
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
        }
        // Handle dates outside the current month
        else {
            holder.parentView.setBackgroundColor(Color.TRANSPARENT); // Default background
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.inactive));
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
