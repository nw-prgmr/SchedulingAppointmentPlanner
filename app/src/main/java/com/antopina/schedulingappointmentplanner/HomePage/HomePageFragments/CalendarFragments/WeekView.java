package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.CalendarFragments;

import static com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils.daysInWeekArray;
import static com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils.monthYearFromDate;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.Event;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.adapter.CalendarAdapter;
import com.antopina.schedulingappointmentplanner.adapter.EventAdapter;
import com.antopina.schedulingappointmentplanner.databinding.FragmentWeekViewBinding;
import com.antopina.schedulingappointmentplanner.utils.BottomDialogHelper;
import com.antopina.schedulingappointmentplanner.utils.EventDataBsaeHelper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

public class WeekView extends Fragment implements CalendarAdapter.OnItemListener {

    private FragmentWeekViewBinding binding;
    private EventDataBsaeHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWeekViewBinding.inflate(inflater, container, false);
        dbHelper = new EventDataBsaeHelper(getContext());

        try {
            if (CalendarUtils.selectedDate == null) {
                CalendarUtils.selectedDate = LocalDate.now();
            }

            setWeekView();
            setListeners();

        } catch (Exception e) {
            Log.e("WeekViewError", "Error initializing view: " + e.getMessage());
            showErrorToast("An error occurred while loading the calendar.");
        }

        return binding.getRoot();
    }

    private void setListeners() {
        binding.btnPreviousWeek.setOnClickListener(v -> handleErrors(this::previousWeekAction));
        binding.btnNextWeek.setOnClickListener(v -> handleErrors(this::nextWeekAction));
        binding.floatingActionButton.setOnClickListener(v -> BottomDialogHelper.showBottomDialog(getContext()));

        binding.weektListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getLastVisiblePosition() == totalItemCount - 1 && view.getChildAt(view.getChildCount() - 1) != null &&
                        view.getChildAt(view.getChildCount() - 1).getBottom() <= view.getHeight()) {
                    binding.floatingActionButton.hide();
                } else if (view.getFirstVisiblePosition() == 0 && view.getChildAt(0) != null &&
                        view.getChildAt(0).getTop() >= 0) {
                    binding.floatingActionButton.show();
                }
            }
        });
    }

    private void setWeekView() {
        try {
            binding.tvmonthYear.setText(monthYearFromDate(CalendarUtils.selectedDate));

            ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);
            if (days == null || days.isEmpty()) {
                throw new IllegalStateException("Days list is empty");
            }

            CalendarAdapter calendarAdapter = new CalendarAdapter(days, this, getContext());
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
            binding.rvcalendar.setLayoutManager(layoutManager);
            binding.rvcalendar.setAdapter(calendarAdapter);

            setEventAndTaskAdapter(); // Display events/tasks for the selected date
        } catch (Exception e) {
            Log.e("WeekViewError", "Error setting week view: " + e.getMessage());
            showErrorToast("Failed to update the calendar view.");
        }
    }

    private void setEventAndTaskAdapter() {
        try {
            // Fetch events and tasks for the selected date
            ArrayList<Event> events = getEventsForDate(CalendarUtils.selectedDate);
            ArrayList<Event> tasks = getTasksForDate(CalendarUtils.selectedDate);

            // Combine events and tasks
            ArrayList<Event> combinedEventsAndTasks = new ArrayList<>();
            combinedEventsAndTasks.addAll(events);
            combinedEventsAndTasks.addAll(tasks);

            // Sort the combined list by dueTime
            combinedEventsAndTasks.sort(new Comparator<Event>() {
                @Override
                public int compare(Event task1, Event task2) {
                    return task1.getTime().compareTo(task2.getTime()); // Compare by dueTime
                }
            });

            // Use EventAdapter for displaying the events and tasks
            EventAdapter eventAdapter = new EventAdapter(getContext(), combinedEventsAndTasks);
            binding.weektListView.setAdapter(eventAdapter); // Set the adapter for ListView
        } catch (Exception e) {
            Log.e("WeekViewError", "Error setting event and task adapter: " + e.getMessage());
            showErrorToast("Failed to load events and tasks.");
        }
    }

    private ArrayList<Event> getEventsForDate(LocalDate date) {
        return dbHelper.getEventsForDate(date.toString());
    }

    private ArrayList<Event> getTasksForDate(LocalDate date) {
        return getTasksForDueDate(date.toString());
    }

    private ArrayList<Event> getTasksForDueDate(String dueDate) {
        ArrayList<Event> taskList = new ArrayList<>();
        Cursor taskCursor = dbHelper.getTasksForDueDate(dueDate);

        if (taskCursor != null) {
            try {
                int nameIndex = taskCursor.getColumnIndex(EventDataBsaeHelper.TASK_COLUMN_NAME);
                int descIndex = taskCursor.getColumnIndex(EventDataBsaeHelper.TASK_COLUMN_DESCRIPTION);
                int dueDateIndex = taskCursor.getColumnIndex(EventDataBsaeHelper.TASK_COLUMN_DUE_DATE);
                int dueTimeIndex = taskCursor.getColumnIndex(EventDataBsaeHelper.TASK_COLUMN_DUE_TIME);
                int statusIndex = taskCursor.getColumnIndex(EventDataBsaeHelper.TASK_COLUMN_STATUS); // Add the status column

                if (nameIndex == -1 || descIndex == -1 || dueDateIndex == -1 || dueTimeIndex == -1 || statusIndex == -1) {
                    Log.e("DatabaseError", "One or more columns are missing in the database schema.");
                    return taskList;
                }

                // Define formatter for 'hh:mm:ss a' format
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");

                while (taskCursor.moveToNext()) {
                    String taskName = taskCursor.getString(nameIndex);
                    String taskDescription = taskCursor.getString(descIndex);
                    String taskDueDate = taskCursor.getString(dueDateIndex);
                    String taskDueTime = taskCursor.getString(dueTimeIndex);
                    String taskStatusStr = taskCursor.getString(statusIndex); // Get task status as a string from the database

                    // Convert the taskStatus string to an integer (use a default value if not a valid number)
                    int taskStatus = 0; // Default value if parsing fails
                    try {
                        taskStatus = Integer.parseInt(taskStatusStr); // Convert taskStatus to integer
                    } catch (NumberFormatException e) {
                        Log.e("DatabaseError", "Invalid task status value: " + taskStatusStr);
                    }

                    try {
                        LocalDate dueDateTask = LocalDate.parse(taskDueDate); // Assuming ISO format
                        LocalTime dueTimeTask = LocalTime.parse(taskDueTime, timeFormatter); // Use custom formatter

                        taskList.add(new Event(taskName, taskDescription, dueDateTask, dueTimeTask, true, taskStatus, dueDateTask, dueTimeTask));
                    } catch (Exception e) {
                        Log.e("TaskParsingError", "Error parsing task data: " + e.getMessage());
                    }
                }
            } finally {
                taskCursor.close();
            }
        } else {
            Log.d("Database", "Task cursor is null for due date: " + dueDate);
        }

        return taskList;
    }

    private void previousWeekAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    private void nextWeekAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date; // Update the selected date
        setEventAndTaskAdapter(); // Refresh the events/tasks for the selected date
    }

    private void handleErrors(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            Log.e("ErrorHandler", "Error executing action: " + e.getMessage());
            showErrorToast("An unexpected error occurred.");
        }
    }

    private void showErrorToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

