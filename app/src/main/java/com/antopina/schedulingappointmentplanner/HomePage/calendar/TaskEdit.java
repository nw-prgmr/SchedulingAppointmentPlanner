package com.antopina.schedulingappointmentplanner.HomePage.calendar;

import android.os.Bundle;
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

import java.time.LocalTime;

public class TaskEdit extends AppCompatActivity {

    private EditText taskNameET, task_description;
    private TextView task_start_date, task_time, task_date, task_repetition;
    private Button saveEvent;

    private Toolbar toolbar;

    private LocalTime time;

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

        task_repetition.setText("No repetition");

        time = LocalTime.now();
        task_date.setText(CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        task_time.setText(CalendarUtils.formattedTime(time));


    }

    private void initWidgets() {
        taskNameET = findViewById(R.id.taskNameET);
        task_description = findViewById(R.id.task_description);
        task_date = findViewById(R.id.task_date);
        task_time = findViewById(R.id.task_time);
        task_repetition = findViewById(R.id.event_repetition);
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
        EventDataBsaeHelper myEventDB = new EventDataBsaeHelper(TaskEdit.this);
        myEventDB.insertTask(taskNameET.getText().toString().trim(),
                task_description.getText().toString().trim());

//        String eventName = taskNameET.getText().toString();
//        String eventDescription = task_description.getText().toString();
//        Event newEvent = new Event(eventName, eventDescription, CalendarUtils.selectedDate, time);
//        Event.eventsList.add(newEvent);
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
}