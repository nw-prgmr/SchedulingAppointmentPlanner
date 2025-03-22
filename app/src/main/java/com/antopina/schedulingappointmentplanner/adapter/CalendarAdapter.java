package com.antopina.schedulingappointmentplanner.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarViewHolder;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.Event;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.utils.EventDataBsaeHelper;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;
    private final Context context;

    // Color variables
    private final int blackColor;
    private final int superLightPink;

    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener, Context context) {
        this.days = days;
        this.onItemListener = onItemListener;
        this.context = context;

        // Initialize colors
        this.blackColor = ContextCompat.getColor(context, R.color.black);
        this.superLightPink = ContextCompat.getColor(context, R.color.super_light_pink);
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        // Set height based on the number of days (week or month view)
        layoutParams.height = days.size() > 15
                ? (int) (parent.getHeight() * 0.166666666) // Month view
                : parent.getHeight(); // Week view
        view.setLayoutParams(layoutParams);

        return new CalendarViewHolder(view, onItemListener, days);
    }

    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        LocalDate date = days.get(position);
        LocalDate today = LocalDate.now();

        // Set the day number
        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));

        // Highlight selected and non-current month days
        holder.dayOfMonth.setSelected(date.equals(CalendarUtils.selectedDate));
        holder.dayOfMonth.setPressed(date.equals(today));
        if (!date.getMonth().equals(CalendarUtils.selectedDate.getMonth())) {
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.super_light_pink));
        }

        // Handle cell clicks
        holder.parentView.setOnClickListener(v -> {
            CalendarUtils.selectedDate = date;
            onItemListener.onItemClick(position, date);
            notifyDataSetChanged();
        });

        // Clear previous events
        holder.eventsContainer.removeAllViews();

        // Fetch and display events and tasks
        EventDataBsaeHelper dbHelper = new EventDataBsaeHelper(context);

        // Add events
        ArrayList<Event> eventList = dbHelper.getEventsForDate(date.toString());
        for (Event event : eventList) {
            TextView textView = createTextView(event.getName(), R.drawable.event_background);
            holder.eventsContainer.addView(textView);
        }

        // Add tasks
        Cursor taskCursor = dbHelper.getTasksForDate(date.toString());
        Log.d("CalendarAdapter", "Tasks for date " + date.toString() + ": " + taskCursor.getCount() + " items found");
        addItemsToContainer(holder.eventsContainer, taskCursor, EventDataBsaeHelper.TASK_COLUMN_NAME, R.drawable.task_background);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    // Method for adding events or tasks to the container
    private void addItemsToContainer(LinearLayout container, Cursor cursor, String columnName, int backgroundResId) {
        if (cursor != null) {
            try {
                int columnIndex = cursor.getColumnIndex(columnName);
                if (columnIndex == -1) {
                    Log.e("CalendarAdapter", "Column not found: " + columnName);
                    return;
                }
                int count = 0;  // Count the number of events or tasks added
                while (cursor.moveToNext()) {
                    String title = cursor.getString(columnIndex);
                    TextView textView = createTextView(title, backgroundResId);
                    container.addView(textView);
                    count++;
                }
                Log.d("CalendarAdapter", "Added " + count + " items to the container for " + columnName);
            } finally {
                cursor.close();
            }
        }
    }

    // Create a text view for event/task item
    private TextView createTextView(String text, int backgroundResId) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(8);
        textView.setSingleLine(true);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundResource(backgroundResId);
        textView.setPadding(5, 2, 5, 2);
        textView.setMinWidth((int) (50 * context.getResources().getDisplayMetrics().density)); // Minimum width 50dp
        textView.setGravity(Gravity.CENTER);

        // Add bottom margin
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, (int) (1 * context.getResources().getDisplayMetrics().density)); // 1dp margin
        textView.setLayoutParams(layoutParams);

        return textView;
    }

    public interface OnItemListener {
        void onItemClick(int position, LocalDate date);
    }
}
