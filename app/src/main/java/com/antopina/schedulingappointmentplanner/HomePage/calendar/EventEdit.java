package com.antopina.schedulingappointmentplanner.HomePage.calendar;

import static com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils.selectedDate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.model.EventModel;
import com.antopina.schedulingappointmentplanner.utils.EventDataBsaeHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventEdit extends AppCompatActivity {

    private EditText eventNameET, event_description;
    private TextView event_start_date, event_start_time, event_end_date, event_end_time, event_repetition;
    private Button saveEvent;

    private Toolbar toolbar;

    private LocalTime eventStartTime, eventEndTime;
    private LocalDate eventStartDate, eventEndDate;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

    EventDataBsaeHelper eventDB;
    ArrayList<String> eventID, eventTitle, eventDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Events"); // Set the title
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back); // Optional: Custom icon
        }

        initWidgets();

        event_repetition.setText("No repetition");

        // Initialize with the current date and time
        eventStartDate = LocalDate.now();
        eventEndDate = LocalDate.now();
        eventStartTime = LocalTime.now();
        eventEndTime = LocalTime.now();

        event_start_date.setText(eventStartDate.format(dateFormatter));
        event_end_date.setText(eventEndDate.format(dateFormatter));
        event_start_time.setText(eventStartTime.format(timeFormatter));
        event_end_time.setText(eventEndTime.format(timeFormatter));

        setListeners();

    }

    private void setListeners() {
        event_start_time.setOnClickListener(v -> showTimePickerDialog(true));
        event_end_time.setOnClickListener(v -> showTimePickerDialog(false));
        event_start_date.setOnClickListener(v -> showDatePickerDialog(true));
        event_end_date.setOnClickListener(v -> showDatePickerDialog(false));
        event_repetition.setOnClickListener(v -> showRepetitionDialog());
    }

    private void initWidgets() {
        eventNameET = findViewById(R.id.eventNameET);
        event_description = findViewById(R.id.event_description);
        event_start_date = findViewById(R.id.event_start_date);
        event_start_time = findViewById(R.id.event_start_time);
        event_end_date = findViewById(R.id.event_end_date);
        event_end_time = findViewById(R.id.event_end_time);
        event_repetition = findViewById(R.id.event_repetition);
        //saveEvent = findViewById(R.id.btSaveEvent);
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
            saveEvent(); // Handle save action
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

    @Override
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

    private void saveEvent() {
        // Get input values from the EditTexts
        String taskName = eventNameET.getText().toString().trim();
        String taskDescription = event_description.getText().toString().trim();

        // Validate inputs
        if (taskName.isEmpty()) {
            Toast.makeText(this, "Event name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (eventStartTime == null || eventEndTime == null) {
            Toast.makeText(this, "Please select both start and end times", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use utility methods to format date and time
        String startDate = CalendarUtils.formattedDate(eventStartDate); // "YYYY-MM-DD"
        String endDate = CalendarUtils.formattedDate(eventEndDate);   // "YYYY-MM-DD"
        String startTime = CalendarUtils.formattedShortTime(eventStartTime); // "HH:mm"
        String endTime = CalendarUtils.formattedShortTime(eventEndTime);     // "HH:mm"

        // Combine start and end date-time into Date objects for comparison
        String startDateTimeString = startDate + " " + startTime;
        String endDateTimeString = endDate + " " + endTime;

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date eventStartDateTime = null;
        Date eventEndDateTime = null;

        try {
            eventStartDateTime = dateTimeFormat.parse(startDateTimeString);
            eventEndDateTime = dateTimeFormat.parse(endDateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date/time format", Toast.LENGTH_SHORT).show();
            return; // Exit if the date/time is invalid
        }

        // Calculate the time difference in hours
        long timeDifferenceInMillis = eventEndDateTime.getTime() - eventStartDateTime.getTime();
        long timeDifferenceInHours = timeDifferenceInMillis / (1000 * 60 * 60);

        // Check if the event duration is at least 12 hours
        if (timeDifferenceInHours < 12) {
            Toast.makeText(this, "Event must last at least 12 hours", Toast.LENGTH_SHORT).show();
            return; // Exit the method without saving
        }

        // Insert the event into the database
        EventDataBsaeHelper myEventDB = new EventDataBsaeHelper(this);
        myEventDB.insertEvent(taskName, taskDescription, startDate, endDate, startTime, endTime);

        // Confirm event saved and finish the activity
        Toast.makeText(this, "Event saved successfully", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }



    private boolean isTimeValid(LocalTime startTime, LocalTime endTime) {
        return !startTime.isAfter(endTime);  // Start time should not be after end time
    }


    private void showRepetitionDialog() {
        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_repetition_options, null);

        // Initialize views from the custom layout
        RadioGroup radioGroup = dialogView.findViewById(R.id.repetition_radio_group);

        // Build the AlertDialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView) // Set the custom view
                .setPositiveButton("OK", (dialog, which) -> {
                    // Get the selected RadioButton
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton selectedRadioButton = dialogView.findViewById(selectedId);

                    if (selectedRadioButton != null) {
                        String selectedOption = selectedRadioButton.getText().toString();
                        if (selectedOption.equals("Custom")) {
                            //showCustomRepetitionDialog(); // Open custom dialog for further options
                        } else {
                            event_repetition.setText(selectedOption); // Update the TextView
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            LocalDate selectedDate = LocalDate.of(year1, month1 + 1, dayOfMonth);
            if (isStartDate) {
                eventStartDate = selectedDate;
                event_start_date.setText(eventStartDate.format(dateFormatter));
            } else {
                if (selectedDate.isBefore(eventStartDate)) {
                    Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
                } else {
                    eventEndDate = selectedDate;
                    event_end_date.setText(eventEndDate.format(dateFormatter));
                }
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog(boolean isStartTime) {
        LocalTime timeToPick = isStartTime ? eventStartTime : eventEndTime;
        int hour = timeToPick.getHour();
        int minute = timeToPick.getMinute();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            LocalTime selectedTime = LocalTime.of(hourOfDay, minute1);
            if (isStartTime) {
                eventStartTime = selectedTime;
                event_start_time.setText(eventStartTime.format(timeFormatter));
            } else {
                if (eventStartDate.isEqual(eventEndDate) && selectedTime.isBefore(eventStartTime)) {
                    Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                } else {
                    eventEndTime = selectedTime;
                    event_end_time.setText(eventEndTime.format(timeFormatter));
                }
            }
        }, hour, minute, false);

        timePickerDialog.show();
    }

}