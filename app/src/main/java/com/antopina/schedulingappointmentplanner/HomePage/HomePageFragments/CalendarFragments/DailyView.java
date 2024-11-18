package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.CalendarFragments;

import static com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils.selectedDate;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.antopina.schedulingappointmentplanner.HomePage.adapter.HourAdapter;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.Event;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.EventEdit;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.HourEvent;
import com.antopina.schedulingappointmentplanner.R;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DailyView extends Fragment {


    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private ListView hourListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_view, container, false);

        try {
            // Ensure selectedDate is initialized
            if (selectedDate == null) {
                selectedDate = LocalDate.now();
            }

            // Initialize widgets
            initWidgets(view);

            // Setup the calendar view
            setDayView();

            // Set click listeners for buttons
            Button previousMonthButton = view.findViewById(R.id.btnPreviousDaily);
            Button nextMonthButton = view.findViewById(R.id.btnNextDaily);

            previousMonthButton.setOnClickListener(v -> handleErrors(this::previousDailyAction));
            nextMonthButton.setOnClickListener(v -> handleErrors(this::nextDailyAction));

            //start event edit
            Button bteventEdit = view.findViewById(R.id.btNewEvent);
            bteventEdit.setOnClickListener(v -> eventEdit());

        } catch (Exception e) {
            showErrorToast("An error occurred while loading the calendar.");
        }

        return view;
    }

    private void initWidgets(View view) {
        monthDayText = view.findViewById(R.id.monthDayText);
        dayOfWeekTV = view.findViewById(R.id.tvDayOfWeek);
        hourListView = view.findViewById(R.id.hourListView);
    }

    @Override
    public void onResume() {
        super.onResume();
        setDayView();
    }

    private void setDayView() {
        monthDayText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekTV.setText(dayOfWeek);
        setHourAdapter();
    }

    private void  setHourAdapter() {
        HourAdapter hourAdapter = new HourAdapter(getContext(), hourEventList());
        hourListView.setAdapter(hourAdapter);
    }

    private ArrayList<HourEvent> hourEventList() {
        ArrayList<HourEvent> list = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            LocalTime time = LocalTime.of(hour, 0);
            ArrayList<Event> events = Event.eventsForDateAndTime(selectedDate, time);
            HourEvent hourEvent = new HourEvent(time, events);
            list.add(hourEvent);
        }

        return list;
    }

    private void previousDailyAction() {
        selectedDate = selectedDate.minusDays(1);
        setDayView();
    }

    private void nextDailyAction() {
        selectedDate = selectedDate.plusDays(1);
        setDayView();
    }

    private void eventEdit() {
        Intent intent = new Intent(getContext(), EventEdit.class);
        startActivity(intent);
    }

    private void handleErrors(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            showErrorToast("An unexpected error occurred.");
        }
    }

    private void showErrorToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}