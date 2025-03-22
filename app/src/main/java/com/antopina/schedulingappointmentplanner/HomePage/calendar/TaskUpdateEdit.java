package com.antopina.schedulingappointmentplanner.HomePage.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.utils.EventDataBsaeHelper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

public class TaskUpdateEdit extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText taskNameET, taskDescriptionET;
    private TextView taskDateTV, taskTimeTV;
    private EventDataBsaeHelper dbHelper;
    private LocalDate selectedDate;
    private LocalTime selectedTime;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_update_edit);
        initToolbar();
        initWidgets();

        // Initialize database helper
        dbHelper = new EventDataBsaeHelper(this);

        // Get task details from intent and populate fields
        retrieveTaskDetailsFromIntent();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Update Task");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back);
        }
    }

    private void initWidgets() {
        taskNameET = findViewById(R.id.updateTaskNameET);
        taskDescriptionET = findViewById(R.id.update_Task_description);
        taskDateTV = findViewById(R.id.update_Task_date);
        taskTimeTV = findViewById(R.id.update_Task_time);

        // Date and time pickers
        taskDateTV.setOnClickListener(v -> showDatePickerDialog());
        taskTimeTV.setOnClickListener(v -> showTimePickerDialog());
    }

    private void retrieveTaskDetailsFromIntent() {
        if (getIntent() != null) {
            Log.d("TaskUpdateEdit", "task_id: " + getIntent().getIntExtra("task_id", taskId));
            Log.d("TaskUpdateEdit", "task_title: " + getIntent().getStringExtra("task_title"));
            Log.d("TaskUpdateEdit", "task_description: " + getIntent().getStringExtra("task_description"));
            Log.d("TaskUpdateEdit", "task_date: " + getIntent().getStringExtra("task_date"));
            Log.d("TaskUpdateEdit", "task_time: " + getIntent().getStringExtra("task_time"));

            taskId = getIntent().getIntExtra("task_id", -1); // Ensure task_id is passed as int

            // Display taskId in a Toast for debugging
            Toast.makeText(this, "Task ID: " + taskId, Toast.LENGTH_SHORT).show();

            if (taskId == -1) {
                Toast.makeText(this, "Invalid task ID", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            taskNameET.setText(getIntent().getStringExtra("task_title"));
            taskDescriptionET.setText(getIntent().getStringExtra("task_description"));
            taskDateTV.setText(getIntent().getStringExtra("task_date"));
            taskTimeTV.setText(getIntent().getStringExtra("task_time"));
        }
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    taskDateTV.setText(selectedDate.toString());
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedTime = LocalTime.of(hourOfDay, minute);
                    taskTimeTV.setText(String.format("%02d:%02d", hourOfDay, minute));
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            updateTask();
            return true;
        } else if (item.getItemId() == R.id.delete) {
//            deleteTask();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Hide other menu items
        menu.findItem(R.id.delete).setVisible(true);
        menu.findItem(R.id.duplicate).setVisible(false);
        menu.findItem(R.id.share).setVisible(false);

        // Ensure the "Save" menu item is visible
        menu.findItem(R.id.save).setVisible(true);

        return true; // Return true to update the menu
    }

    private void updateTask() {
        String title = taskNameET.getText().toString().trim();
        String description = taskDescriptionET.getText().toString().trim();
        String date = taskDateTV.getText().toString();
        String time = taskTimeTV.getText().toString();

        if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }



        boolean isUpdated = dbHelper.updateTask(taskId, title, description, date, time);

        if (isUpdated) {
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
        }
    }

//    private void deleteTask(Task task, int position) {
//        EventDataBsaeHelper dbHelper = new EventDataBsaeHelper(context);
//        boolean isDeleted = dbHelper.deleteTask(String.valueOf(task.getId()));
//
//        if (isDeleted) {
//            tasks.remove(position);
//            notifyItemRemoved(position);
//            Toast.makeText(context, "Task deleted.", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(context, "Error deleting task.", Toast.LENGTH_SHORT).show();
//        }
//    }

}
