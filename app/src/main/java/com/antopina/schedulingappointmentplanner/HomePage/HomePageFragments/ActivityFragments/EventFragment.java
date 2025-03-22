package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.ActivityFragments;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.antopina.schedulingappointmentplanner.HomePage.calendar.EventEdit;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.TaskEdit;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.adapter.EventsAdapter;
import com.antopina.schedulingappointmentplanner.databinding.FragmentEventBinding;
import com.antopina.schedulingappointmentplanner.model.EventModel;
import com.antopina.schedulingappointmentplanner.utils.BottomDialogHelper;
import com.antopina.schedulingappointmentplanner.utils.EventDataBsaeHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EventFragment extends Fragment {

    FragmentEventBinding binding;

    private EventDataBsaeHelper eventDB;
    private ArrayList<EventModel> eventList;
    private EventsAdapter eventsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEventBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize the RecyclerView
        RecyclerView eventRecycler = binding.eventRecycler;
        eventRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize database helper
        eventDB = new EventDataBsaeHelper(getContext());

        // Initialize the ArrayList for event data
        eventList = new ArrayList<>();

        // Fetch data from database
        fetchEventData();

        // Set up the adapter
        eventsAdapter = new EventsAdapter(getContext(), eventList);
        eventRecycler.setAdapter(eventsAdapter);

        // Show or hide "Add new Event." text based on event list size
        TextView noEventTextView = binding.noEventTV;
        if (eventList.isEmpty()) {
            noEventTextView.setVisibility(View.VISIBLE); // Show text if no events
            eventRecycler.setVisibility(View.GONE); // Hide RecyclerView
        } else {
            noEventTextView.setVisibility(View.GONE); // Hide text if there are events
            eventRecycler.setVisibility(View.VISIBLE); // Show RecyclerView
        }

        binding.eventRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    binding.eventFloatingActionButton.hide();
                } else if (dy < 0) {
                    binding.eventFloatingActionButton.show();
                }
            }
        });

        binding.eventFloatingActionButton.setOnClickListener(v -> BottomDialogHelper.showBottomDialog(getContext()));

        return view;
    }

    private void fetchEventData() {
        Cursor cursor = eventDB.readAllData();
        if (cursor != null) {
            try {
                if (cursor.getCount() == 0) {
                    Toast.makeText(getContext(), "No Events Found", Toast.LENGTH_SHORT).show();
                } else {
                    DateTimeFormatter dbDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");

                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(0); // Fetch ID as a long
                        String title = cursor.getString(1);
                        String description = cursor.getString(2);

                        // Parse and format dates
                        String startDate = cursor.getString(3);
                        String endDate = cursor.getString(4);
                        LocalDate parsedStartDate = LocalDate.parse(startDate, dbDateFormatter);
                        LocalDate parsedEndDate = LocalDate.parse(endDate, dbDateFormatter);

                        String formattedStartDate = parsedStartDate.format(displayDateFormatter);
                        String formattedEndDate = parsedEndDate.format(displayDateFormatter);

                        String startTime = cursor.getString(5);
                        String endTime = cursor.getString(6);

                        // Create an EventModel object and add it to the list
                        EventModel event = new EventModel(id, title, description, formattedStartDate, formattedEndDate, startTime, endTime);
                        eventList.add(event);
                    }
                }
            } catch (Exception e) {
                Log.e("EventFragment", "Error reading database: " + e.getMessage());
                Toast.makeText(getContext(), "Error reading events.", Toast.LENGTH_SHORT).show();
            } finally {
                cursor.close();
            }
        } else {
            Log.e("EventFragment", "Cursor is null.");
        }
    }


}
