package com.antopina.schedulingappointmentplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.antopina.schedulingappointmentplanner.R;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private Context context;
    private ArrayList event_id, event_title, event_description;

    public TaskAdapter(Context context, ArrayList event_id, ArrayList event_title,
                ArrayList event_description) {
        this.context = context;
        this.event_id = event_id;
        this.event_title = event_title;
        this.event_description = event_description;
    }

    @NonNull
    @Override
    public TaskAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_task, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.MyViewHolder holder, int position) {
        holder.event_title_text.setText(String.valueOf(event_title.get(position)));
        holder.event_description_text.setText(String.valueOf(event_description.get(position)));

        // Handle popup menu
        holder.options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.item_task_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.menuDelete) {
                    Toast.makeText(context, "Delete clicked for: " + event_title.get(position), Toast.LENGTH_SHORT).show();
                    // Handle delete action
                    return true;

                } else if (itemId == R.id.menuUpdate) {
                    Toast.makeText(context, "Update clicked for: " + event_title.get(position), Toast.LENGTH_SHORT).show();
                    // Handle update action
                    return true;

                } else if (itemId == R.id.menuComplete) {
                    Toast.makeText(context, "Complete clicked for: " + event_title.get(position), Toast.LENGTH_SHORT).show();
                    // Handle complete action
                    return true;

                } else {
                    return false;
                }
            });

            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return event_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView event_id_text, event_title_text, event_description_text;
        ImageView options;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            event_title_text = itemView.findViewById(R.id.title);
            event_description_text = itemView.findViewById(R.id.description);
            options = itemView.findViewById(R.id.options);
        }
    }
}
