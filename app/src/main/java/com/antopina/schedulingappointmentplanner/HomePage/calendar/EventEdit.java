package com.antopina.schedulingappointmentplanner.HomePage.calendar;

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
import com.antopina.schedulingappointmentplanner.utils.EventDataBsaeHelper;

import java.time.LocalTime;
import java.util.ArrayList;

public class EventEdit extends AppCompatActivity {

    private EditText eventNameET, event_description;
    private TextView event_start_date, event_start_time, event_end_date, event_end_time, event_repetition;
    private Button saveEvent;

    private Toolbar toolbar;

    private LocalTime time;

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

        time = LocalTime.now();
        event_start_date.setText(CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        event_start_time.setText(CalendarUtils.formattedTime(time));
        event_end_date.setText(CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        event_end_time.setText(CalendarUtils.formattedTime(time));

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
            deleteEvent(); // Handle delete action
            return true;
        } else if (id == R.id.duplicate) {
            duplicateEvent(); // Handle duplicate action
            return true;
        } else if (id == R.id.share) {
            shareEvent(); // Handle share action
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

    public void saveEvent() {
        EventDataBsaeHelper myEventDB = new EventDataBsaeHelper(EventEdit.this);
        myEventDB.insertEvent(eventNameET.getText().toString().trim(),
                event_description.getText().toString().trim());

        String eventName = eventNameET.getText().toString();
        String eventDescription = event_description.getText().toString();
        Event newEvent = new Event(eventName, eventDescription, CalendarUtils.selectedDate, time);
        Event.eventsList.add(newEvent);
        finish();
    }

    private void deleteEvent() {
        // Handle delete event logic
        Toast.makeText(this, "Delete action selected", Toast.LENGTH_SHORT).show();
    }

    private void duplicateEvent() {
        // Handle duplicate event logic
        Toast.makeText(this, "Duplicate action selected", Toast.LENGTH_SHORT).show();
    }

    private void shareEvent() {
        // Handle share event logic
        Toast.makeText(this, "Share action selected", Toast.LENGTH_SHORT).show();
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


}