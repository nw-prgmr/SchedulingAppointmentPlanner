package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.ActivityFragments;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.antopina.schedulingappointmentplanner.BroadcastReceiver.AlarmReceiver;
import com.antopina.schedulingappointmentplanner.HomePage.HomePageView;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.adapter.TaskAdapter;
import com.antopina.schedulingappointmentplanner.databinding.FragmentTaskBinding;
import com.antopina.schedulingappointmentplanner.model.Task;
import com.antopina.schedulingappointmentplanner.utils.BottomDialogHelper;
import com.antopina.schedulingappointmentplanner.utils.EventDataBsaeHelper;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TaskFragment extends Fragment {

    private FragmentTaskBinding binding;
    private EventDataBsaeHelper eventDB;

    // RecyclerViews and Adapters
    private TaskAdapter taskAdapter;

    // Task data lists
    private ArrayList<Task> tasks = new ArrayList<>();
    private AlarmManager alarmManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        eventDB = new EventDataBsaeHelper(getContext());
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        createNotificationChannel();

        // Check and request notification permission if needed

        // Setup RecyclerViews
        binding.taskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Adapters
        taskAdapter = new TaskAdapter(getContext(), tasks);
        binding.taskRecycler.setAdapter(taskAdapter);

        // Load tasks and set alarms
        loadTasks();


        // Floating Action Button visibility control
        binding.taskRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    binding.floatingActionButton.hide();
                } else if (dy < 0) {
                    binding.floatingActionButton.show();
                }
            }
        });

        // Floating Action Button click listener
        binding.floatingActionButton.setOnClickListener(v -> BottomDialogHelper.showBottomDialog(getContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        tasks.clear();

        try (Cursor cursor = eventDB.readAllTasks()) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int status = cursor.getInt(3); // Assuming status is in the 4th column
                    if (status == 0) {  // Incomplete tasks
                        Task task = new Task(
                                cursor.getLong(0),    // ID
                                cursor.getString(1),  // Title
                                cursor.getString(2),  // Description
                                cursor.getString(4),  // Date
                                cursor.getString(5),  // Time
                                status                // Status
                        );
                        tasks.add(task);
                        setTaskAlarm(task);  // Set alarm only for tasks in the future
                    }
                }
            }
        }

        if (tasks.isEmpty()) {
            binding.noTaskTV.setVisibility(View.VISIBLE);
            binding.taskRecycler.setVisibility(View.GONE);
        } else {
            binding.noTaskTV.setVisibility(View.GONE);
            binding.taskRecycler.setVisibility(View.VISIBLE);
            taskAdapter.notifyDataSetChanged();
        }
    }

    private void setTaskAlarm(Task task) {
        try {
            // Check for permission to set exact alarms (from Android 12 onwards)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!getContext().getSystemService(AlarmManager.class).canScheduleExactAlarms()) {
                    // Request permission if not granted
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(intent);
                    return; // Exit early as we cannot set the alarm without permission
                }
            }

            // Parse the date and time
            String dateTimeString = task.getDate() + " " + task.getTime();
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            dateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date taskDateTime = dateTimeFormat.parse(dateTimeString);

            // Convert system time to UTC for accurate comparison
            long currentTimeUTC = System.currentTimeMillis();
            if (taskDateTime != null && taskDateTime.getTime() > currentTimeUTC) {
                showNotification(task);
            } else {
                Log.e("TaskAlarm", "Notification not shown because the due date/time is in the past.");
            }

            if (taskDateTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(taskDateTime);

                // Log the task date/time and current time in millis for comparison
                Log.d("TaskAlarm", "Task DateTime: " + taskDateTime.toString());
                Log.d("TaskAlarm", "Current Time: " + new Date(System.currentTimeMillis()).toString());
                Log.d("TaskAlarm", "Task Time in millis: " + calendar.getTimeInMillis());
                Log.d("TaskAlarm", "Current Time in millis: " + System.currentTimeMillis());

                // Check if the alarm time is in the future
                if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                    // Create the alarm and show notification
                    showNotification(task);

                    // Set the alarm
                    Intent intent = new Intent(getContext(), AlarmReceiver.class);
                    intent.putExtra("task_title", task.getTitle());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            getContext(),
                            (int) task.getId(),
                            intent,
                            PendingIntent.FLAG_IMMUTABLE // Add this flag to make it immutable
                    );

                    // Use AlarmManager to set the alarm at the task time
                    AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent
                        );
                    } else {
                        alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent
                        );
                    }

                    Log.d("TaskAlarm", "Alarm successfully set for: " + task.getTitle());
                } else {
                    // Log if the time is in the past
                    Log.e("TaskAlarm", "The time for " + task.getTitle() + " is in the past. No alarm set.");
                }
            } else {
                // Log parsing failure
                Log.e("TaskAlarm", "Failed to parse date/time for: " + task.getTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TaskAlarm", "Error setting alarm for: " + task.getTitle(), e);
        }
    }

    // This method should show a notification when the task is due
    private void showNotification(Task task) {
        // Check if the task's due date and time are within 15 minutes of the current time
        String dateTimeString = task.getDate() + " " + task.getTime();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        // Set the time zone to Philippines (UTC+8)
        TimeZone philippinesTimeZone = TimeZone.getTimeZone("Asia/Manila");
        dateTimeFormat.setTimeZone(philippinesTimeZone);

        try {
            Date taskDateTime = dateTimeFormat.parse(dateTimeString);
            if (taskDateTime != null) {
                long currentTime = System.currentTimeMillis();

                // Adjust current time to Philippines time zone
                Calendar currentCalendar = Calendar.getInstance(philippinesTimeZone);
                long currentTimeInPhilippines = currentCalendar.getTimeInMillis();

                long taskTime = taskDateTime.getTime();

                // Calculate the difference between the current time and the task's due time
                long timeDifference = taskTime - currentTimeInPhilippines;

                // Check if the task's due time is within 15 minutes (900,000 milliseconds) from now
                if (timeDifference <= 900000 && timeDifference >= 0) {
                    // Task is due now or within 15 minutes from now, show notification
                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    String channelId = "task_alarm_channel";

                    // For Android 8.0 and above, create a Notification Channel
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        CharSequence name = "Task Alarms";
                        String description = "Notifications for task alarms";
                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
                        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
                        channel.setDescription(description);
                        notificationManager.createNotificationChannel(channel);
                    }

                    // Build the notification
                    Notification notification = new NotificationCompat.Builder(getContext(), channelId)
                            .setContentTitle("Task to do")
                            .setContentText("Reminder: " + task.getTitle())
                            .setSmallIcon(R.drawable.bell) // Your app icon here
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .setContentIntent(getPendingIntentForTask(task)) // Open the task when clicked
                            .build();

                    // Show the notification
                    notificationManager.notify((int) task.getId(), notification);
                } else {
                    Log.e("TaskAlarm", "Notification not shown because the due date/time is more than 15 minutes away.");
                }
            } else {
                Log.e("TaskAlarm", "Error parsing date for notification: " + task.getTitle());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("TaskAlarm", "Error parsing date for notification: " + task.getTitle());
        }
    }



    // Create a PendingIntent to open the task when the notification is clicked
    private PendingIntent getPendingIntentForTask(Task task) {
        Intent nextActivity = new Intent(getContext(), HomePageView.class);
        nextActivity.putExtra("open_fragment", "activity_fragment"); // Specify the fragment to open
        nextActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        nextActivity.putExtra("task_id", task.getId());

        return PendingIntent.getActivity(getContext(), (int) task.getId(), nextActivity, PendingIntent.FLAG_IMMUTABLE);
    }




    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ScheDue";
            String description = "Schedueeee";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("androidknowledge", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
