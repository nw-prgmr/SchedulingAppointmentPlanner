package com.antopina.schedulingappointmentplanner.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.antopina.schedulingappointmentplanner.HomePage.calendar.TaskEdit;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.TaskUpdateEdit;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.model.Task;
import com.antopina.schedulingappointmentplanner.utils.EventDataBsaeHelper;

import java.time.LocalDate;
import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    private final ArrayList<Task> tasks;

    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        setupTaskData(holder, task);
        setupPopupMenu(holder, task, position);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private void setupTaskData(TaskViewHolder holder, Task task) {
        // Set task title
        holder.taskTitle.setText(task.getTitle());

        // Set task description or fallback to "No Description"
        String description = task.getDescription();
        holder.taskDescription.setText(description == null || description.isEmpty() ? "No Description" : description);

        // Set task time
        holder.taskTime.setText(task.getTime());

        // Format and set task date
        parseAndSetDate(holder, task.getDate());

        // Set task status
        setTaskStatus(holder.statusTextView, task.getStatus());


    }

    private void parseAndSetDate(TaskViewHolder holder, String taskDate) {
        try {
            LocalDate localDate = LocalDate.parse(taskDate);
            holder.taskDay.setText(localDate.getDayOfWeek().name().substring(0, 3));
            holder.taskDate.setText(String.valueOf(localDate.getDayOfMonth()));
            holder.taskMonth.setText(localDate.getMonth().name().substring(0, 3));
        } catch (Exception e) {
            e.printStackTrace();
            holder.taskDay.setText("-");
            holder.taskDate.setText("-");
            holder.taskMonth.setText("-");
        }
    }

    private void setTaskStatus(TextView statusTextView, int status) {
        if (status == 0) { // Upcoming task
            statusTextView.setText("UPCOMING");
            statusTextView.setTextColor(Color.BLACK);
        } else if (status == 1) { // Completed task
            statusTextView.setText("COMPLETED");
            statusTextView.setTextColor(Color.GRAY);
        }
    }

    private void setupPopupMenu(TaskViewHolder holder, Task task, int position) {
        holder.options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.item_task_menu, popupMenu.getMenu());

            // Hide the "Update" and "Complete" menu items if the task is already completed
            if (task.getStatus() == 1) {
//                popupMenu.getMenu().findItem(R.id.menuUpdate).setVisible(false);
                popupMenu.getMenu().findItem(R.id.menuComplete).setVisible(false);
            }

            popupMenu.setOnMenuItemClickListener(item -> handleMenuItemClick(item.getItemId(), task, position));
            popupMenu.show();
        });
    }

    private boolean handleMenuItemClick(int itemId, Task task, int position) {
        if (itemId == R.id.menuDelete) {
            showDeleteConfirmationDialog(task, position);
            return true;
        } else if (itemId == R.id.menuComplete) {
            markTaskAsCompleted(task, position);
            return true;
        } else {
            return false;
        }
//        else if (itemId == R.id.menuUpdate) {
//            navigateToTaskEdit(task);
//            return true;
//        }

    }

    private void showDeleteConfirmationDialog(Task task, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null);

        TextView taskTitleTextView = dialogView.findViewById(R.id.alert_task_title);
        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        Button btnNo = dialogView.findViewById(R.id.btn_no);

        taskTitleTextView.setText("Delete " + task.getTitle() + " ?");

        Dialog dialog = new Dialog(context);
        dialog.setContentView(dialogView);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnYes.setOnClickListener(view -> {
            deleteTask(task, position);
            dialog.dismiss();
        });

        btnNo.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private void deleteTask(Task task, int position) {
        EventDataBsaeHelper dbHelper = new EventDataBsaeHelper(context);
        boolean isDeleted = dbHelper.deleteTask(String.valueOf(task.getId()));

        if (isDeleted) {
            tasks.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Task deleted.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error deleting task.", Toast.LENGTH_SHORT).show();
        }
    }


    private void markTaskAsCompleted(Task task, int position) {
        EventDataBsaeHelper dbHelper = new EventDataBsaeHelper(context);
        boolean isUpdated = dbHelper.updateTaskStatus(task.getId(), 1); // 1 means completed

        if (isUpdated) {
            task.setStatus(1);
            notifyItemChanged(position);
            Toast.makeText(context, "Task marked as completed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to mark task as completed", Toast.LENGTH_SHORT).show();
        }
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDescription, taskDay, taskDate, taskMonth, taskTime, statusTextView;
        ImageView options;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.title);
            taskDescription = itemView.findViewById(R.id.description);
            taskDay = itemView.findViewById(R.id.day);
            taskDate = itemView.findViewById(R.id.date);
            taskMonth = itemView.findViewById(R.id.month);
            taskTime = itemView.findViewById(R.id.time);
            statusTextView = itemView.findViewById(R.id.status);
            options = itemView.findViewById(R.id.options);
        }
    }


}
