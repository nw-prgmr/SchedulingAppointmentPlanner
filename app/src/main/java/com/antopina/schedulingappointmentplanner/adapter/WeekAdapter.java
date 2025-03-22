package com.antopina.schedulingappointmentplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.model.WeekItem;

import java.util.List;

public class WeekAdapter extends BaseAdapter {

    private final Context context;
    private final List<WeekItem> weekItems;

    public WeekAdapter(Context context, List<WeekItem> weekItems) {
        this.context = context;
        this.weekItems = weekItems;
    }

    @Override
    public int getCount() {
        return weekItems.size();
    }

    @Override
    public Object getItem(int position) {
        return weekItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.week_item_list, parent, false);
        }

        WeekItem weekItem = weekItems.get(position);

        TextView title = convertView.findViewById(R.id.title);
        TextView time = convertView.findViewById(R.id.time);
        TextView description = convertView.findViewById(R.id.description);
        View activityTypeTV = convertView.findViewById(R.id.activityTypeTV);

        // Set text values
        title.setText(weekItem.getTitle());
        time.setText(weekItem.getTime());
        description.setText(weekItem.getDescription());

        // Set background color based on activity type
        if (weekItem.isActivity()) {
            activityTypeTV.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
        } else {
            activityTypeTV.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));
        }

        return convertView;
    }
}
