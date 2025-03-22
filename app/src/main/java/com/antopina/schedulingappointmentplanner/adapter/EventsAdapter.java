package com.antopina.schedulingappointmentplanner.adapter;

import android.app.Dialog;
import android.content.Context;
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

import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.model.EventModel;
import com.antopina.schedulingappointmentplanner.model.Task;
import com.antopina.schedulingappointmentplanner.utils.EventDataBsaeHelper;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {

    private final Context context;
    private final ArrayList<EventModel> eventList;

    public EventsAdapter(Context context, ArrayList<EventModel> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_task layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {
        // Get the event at the current position
        EventModel event = eventList.get(position);

        // Bind data to views
        holder.eventTitle.setText(event.getTitle());

        String description = event.getDescription();
        if (description == null || description.isEmpty()) {
            holder.eventDescription.setText("No description");
        } else {
            holder.eventDescription.setText(description);
        }

        holder.eventStartTime.setText(String.format("%s - %s", event.getStartTime(), event.getEndTime()));
        holder.eventEndTime.setText(String.format("%s - %s", event.getStartTime(), event.getEndTime()));
        holder.eventStartDay.setText(event.getStartDate());
        holder.eventEndDate.setText(event.getEndDate());

        // Set up the popup menu with the correct parameters
        setupPopupMenu(holder.options, event, position);
    }


    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private void setupPopupMenu(ImageView options, EventModel eventModel, int position) {
        options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.item_task_menu, popupMenu.getMenu());

            // Hide "Update" menu item if needed (e.g., based on some condition)
            popupMenu.getMenu().findItem(R.id.menuComplete).setVisible(false);

            // Set the click listener for menu items
            popupMenu.setOnMenuItemClickListener(item -> handleMenuItemClick(item.getItemId(), eventModel, position));
            popupMenu.show();
        });
    }



    private boolean handleMenuItemClick(int itemId, EventModel eventModel, int position) {
        if (itemId == R.id.menuDelete) {
            showDeleteConfirmationDialog(eventModel, position);
            return true;
        } else if (itemId == R.id.menuComplete) {

            return true;
        } else {
            return false;
        }
//        else if (itemId == R.id.menuUpdate) {
//            navigateToTaskEdit(task);
//            return true;
//        }

    }

    private void showDeleteConfirmationDialog(EventModel eventModel, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null);

        TextView taskTitleTextView = dialogView.findViewById(R.id.alert_task_title);
        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        Button btnNo = dialogView.findViewById(R.id.btn_no);

        taskTitleTextView.setText("Delete " + eventModel.getTitle() + " ?");

        Dialog dialog = new Dialog(context);
        dialog.setContentView(dialogView);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnYes.setOnClickListener(view -> {
            deleteEvent(eventModel, position); // Call delete logic
            dialog.dismiss();
        });

        btnNo.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }



    private void deleteEvent(EventModel eventModel, int position) {
        EventDataBsaeHelper dbHelper = new EventDataBsaeHelper(context);
        boolean isDeleted = dbHelper.deleteEvent(String.valueOf(eventModel.getId()));

        if (isDeleted) {
            // Remove the item from the list
            eventList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, eventList.size());
            Toast.makeText(context, "Event deleted successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error deleting event.", Toast.LENGTH_SHORT).show();
        }
    }

    // ViewHolder class
    public static class EventsViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle, eventDescription, eventStartDay, eventEndDate, eventStartTime, eventEndTime;
        ImageView options;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.title);
            eventDescription = itemView.findViewById(R.id.description);
            eventStartDay = itemView.findViewById(R.id.startDate);
            eventEndDate = itemView.findViewById(R.id.endDate);
            eventStartTime = itemView.findViewById(R.id.startTime);
            eventEndTime = itemView.findViewById(R.id.endTime);
            options = itemView.findViewById(R.id.options);
        }
    }
}
