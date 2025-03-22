package com.antopina.schedulingappointmentplanner.HomePage.calendar;

import static com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils.selectedDate;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.utils.EventDataBsaeHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TaskEdit extends AppCompatActivity {

    private EditText taskNameET, task_description;
    private TextView task_start_date, task_time, task_date, task_repetition;

    private Toolbar toolbar;

    private LocalDate date;
    private LocalTime selectedTime;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("New Task"); // Set the title
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back); // Optional: Custom icon
        }

        initWidgets();


        selectedTime = LocalTime.now();
        date = LocalDate.now();

        task_time.setText(CalendarUtils.formattedTime(selectedTime));
        task_date.setText(CalendarUtils.formattedDate(date));

        task_repetition.setText("No Repetition");

        // Update task logic here
        setupListeners();
    }

    private void initWidgets() {
        taskNameET = findViewById(R.id.taskNameET);
        task_description = findViewById(R.id.task_description);
        task_date = findViewById(R.id.task_date);
        task_time = findViewById(R.id.task_time);
        task_repetition = findViewById(R.id.event_repetition);
        //saveEvent = findViewById(R.id.btSaveEvent);
    }

    private void setupListeners() {
        // Show Date Picker Dialog when task_date is clicked
        task_date.setOnClickListener(v -> showDatePickerDialog());

        // Show Time Picker Dialog when task_time is clicked
        task_time.setOnClickListener(v -> showTimePickerDialog());
    }

    private void showDatePickerDialog() {
        // Get the current date or the previously selected date
        int year = selectedDate != null ? selectedDate.getYear() : Calendar.getInstance().get(Calendar.YEAR);
        int month = selectedDate != null ? selectedDate.getMonthValue() - 1 : Calendar.getInstance().get(Calendar.MONTH); // Calendar months are 0-based
        int day = selectedDate != null ? selectedDate.getDayOfMonth() : Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    // Update the selectedDate and TextView
                    selectedDate = LocalDate.of(year1, month1 + 1, dayOfMonth); // Adjust for 0-based month
                    task_date.setText(CalendarUtils.formattedDate(selectedDate));
                }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        // Get the current time or the previously selected time
        int hour = selectedTime != null ? selectedTime.getHour() : Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = selectedTime != null ? selectedTime.getMinute() : Calendar.getInstance().get(Calendar.MINUTE);

        // Create and show the TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    // Update the selectedTime and TextView
                    selectedTime = LocalTime.of(hourOfDay, minute1);
                    task_time.setText(CalendarUtils.formattedTime(selectedTime));
                }, hour, minute, false); // `false` for 12-hour format with AM/PM

        timePickerDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event,menu);
        return true;
    }

    // Handle toolbar menu clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); // Get the selected item's ID

        if (id == R.id.save) {
            saveTask(); // Handle save action
            return true;
        } else if (id == R.id.delete) {
            return true;
        } else if (id == R.id.duplicate) {
            return true;
        } else if (id == R.id.share) {
            return true;
        } else if (id == android.R.id.home) { // Back button handling
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Hide other menu items
        menu.findItem(R.id.delete).setVisible(false);
        menu.findItem(R.id.duplicate).setVisible(false);
        menu.findItem(R.id.share).setVisible(false);

        // Ensure the "Save" menu item is visible
        menu.findItem(R.id.save).setVisible(true);

        return true; // Return true to update the menu
    }

    private void saveTask() {
        String taskName = taskNameET.getText().toString().trim();
        String taskDescription = task_description.getText().toString().trim();

        // Check if task name is empty
        if (taskName.isEmpty()) {
            Toast.makeText(this, "Task title cannot be empty", Toast.LENGTH_SHORT).show();
            return; // Exit the method without saving
        }

        // Separate the date and time
        String dueDate = task_date.getText().toString();
        String dueTime = task_time.getText().toString();

        // Combine the date and time into a single string for comparison
        String dateTimeString = dueDate + " " + dueTime;

        // Initialize the date format and parse the due time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date taskDateTime = null;

        try {
            taskDateTime = dateFormat.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date/time format", Toast.LENGTH_SHORT).show();
            return; // Exit if the date/time is invalid
        }

        // Get the current time and check if the task time is in the past or equal to now
        long currentTimeMillis = System.currentTimeMillis();
        long taskTimeMillis = taskDateTime.getTime();

        // Check if the task time is the same as the current time
        if (taskTimeMillis == currentTimeMillis) {
            Toast.makeText(this, "Task time cannot be equal to the current time", Toast.LENGTH_SHORT).show();
            return; // Exit if the task time is equal to the current time
        }

        // Check if the task time is in the past or equal to now
        if (taskTimeMillis < currentTimeMillis) {
            Toast.makeText(this, "Task time cannot be in the past", Toast.LENGTH_SHORT).show();
            return; // Exit if the task time is in the past
        }

        // Calculate the time difference in minutes
        long timeDifferenceInMinutes = (taskTimeMillis - currentTimeMillis) / (1000 * 60);

        // Check if the task is at least 30 minutes in the future
        if (timeDifferenceInMinutes < 30) {
            Toast.makeText(this, "Task must be at least 30 minutes in the future", Toast.LENGTH_SHORT).show();
            return; // Exit the method without saving
        }

        // Initialize the database helper
        EventDataBsaeHelper myEventDB = new EventDataBsaeHelper(TaskEdit.this);

        // Insert new task if taskId is -1
        long newTaskId = myEventDB.insertTask(taskName, taskDescription, dueDate, dueTime);
        if (newTaskId != -1) {
            // Task saved successfully, you can add any additional logic here if needed
            Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Handle insertion error
            Toast.makeText(this, "Error adding task", Toast.LENGTH_SHORT).show();
        }

        // Return to previous activity
        setResult(RESULT_OK);
        finish();
    }



}