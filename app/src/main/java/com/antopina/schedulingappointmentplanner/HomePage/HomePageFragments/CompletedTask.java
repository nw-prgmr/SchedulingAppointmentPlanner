package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.antopina.schedulingappointmentplanner.HomePage.HomePageView;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.adapter.TaskAdapter;
import com.antopina.schedulingappointmentplanner.databinding.ActivityCompletedTaskBinding;
import com.antopina.schedulingappointmentplanner.model.Task;
import com.antopina.schedulingappointmentplanner.utils.EventDataBsaeHelper;

import java.util.ArrayList;

public class CompletedTask extends AppCompatActivity {


    private ActivityCompletedTaskBinding binding;
    private EventDataBsaeHelper eventDB;
    private TaskAdapter completeTaskAdapter;
    private ArrayList<Task> completedTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompletedTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventDB = new EventDataBsaeHelper(this);

        binding.completeTaskRecycler.setLayoutManager(new LinearLayoutManager(this));

        completeTaskAdapter = new TaskAdapter(this, completedTasks);
        binding.completeTaskRecycler.setAdapter(completeTaskAdapter);

        binding.backButton.setOnClickListener(v -> navigateToHomePageWithProfile());

        binding.deleteAllComplete.setOnClickListener(v -> showDeleteAllConfirmationDialog());

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCompletedTasks();
    }

    private void loadCompletedTasks() {
        completedTasks.clear();

        try (Cursor cursor = eventDB.readAllTasks()) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int status = cursor.getInt(3); // Assuming status is in the 4th column
                    if (status == 1) {  // Completed tasks
                        Task task = new Task(
                                cursor.getLong(0),    // ID
                                cursor.getString(1),  // Title
                                cursor.getString(2),  // Description
                                cursor.getString(4),  // Date
                                cursor.getString(5),  // Time
                                status                // Status
                        );
                        completedTasks.add(task);
                    }
                }
            }
        }

        if (completedTasks.isEmpty()) {
            binding.noTaskCompletedTV.setVisibility(View.VISIBLE);
        } else {
            binding.noTaskCompletedTV.setVisibility(View.GONE);
            completeTaskAdapter.notifyDataSetChanged();
        }
    }

    private void showDeleteAllConfirmationDialog() {
        if (completedTasks.isEmpty()) {
            // Show a toast if there are no completed tasks
            Toast.makeText(this, "There are no completed tasks to delete.", Toast.LENGTH_SHORT).show();
            return; // Exit the method
        }

        // Inflate the custom alert dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog, null);

        TextView taskTitleTextView = dialogView.findViewById(R.id.alert_task_title);
        TextView taskDescriptionTextView = dialogView.findViewById(R.id.alert_message);
        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        Button btnNo = dialogView.findViewById(R.id.btn_no);

        // Set the title and description for the dialog
        taskTitleTextView.setText("Delete All Tasks?");
        taskDescriptionTextView.setText("Are you sure you want to delete all completed tasks? This action cannot be undone.");

        // Create and configure the dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogView);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Handle "Yes" button click
        btnYes.setOnClickListener(view -> {
            deleteAllCompletedTasks(); // Call the deletion method
            dialog.dismiss();
        });

        // Handle "No" button click
        btnNo.setOnClickListener(view -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }


    private void deleteAllCompletedTasks() {
        // Delete tasks in the database
        eventDB.deleteAllCompletedTasks();

        // Clear the local list and refresh the adapter
        completedTasks.clear();
        completeTaskAdapter.notifyDataSetChanged();

        // Update the UI
        binding.noTaskCompletedTV.setVisibility(View.VISIBLE);
    }


    // Navigate to ProfileFragment when the back button is clicked
    private void navigateToHomePageWithProfile() {
        Intent intent = new Intent(this, HomePageView.class);
        intent.putExtra("open_profile", true); // Flag to open ProfileFragment
        startActivity(intent);
        finish();
    }


}