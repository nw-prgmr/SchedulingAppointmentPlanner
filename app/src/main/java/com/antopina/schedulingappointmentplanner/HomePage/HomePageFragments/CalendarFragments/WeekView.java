package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.CalendarFragments;

import static com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils.daysInWeekArray;
import static com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.antopina.schedulingappointmentplanner.HomePage.Settings;
import com.antopina.schedulingappointmentplanner.HomePage.adapter.CalendarAdapter;
import com.antopina.schedulingappointmentplanner.HomePage.adapter.EventAdapter;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.Event;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.EventEdit;
import com.antopina.schedulingappointmentplanner.R;

import java.time.LocalDate;
import java.util.ArrayList;


public class WeekView extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_view, container, false);

        try {
            // Ensure selectedDate is initialized
            if (CalendarUtils.selectedDate == null) {
                CalendarUtils.selectedDate = LocalDate.now();
            }

            // Initialize widgets
            initWidgets(view);

            // Setup the calendar view
            setWeekView();

            // Set click listeners for buttons
            Button previousMonthButton = view.findViewById(R.id.btnPreviousWeek);
            Button nextMonthButton = view.findViewById(R.id.btnNextWeek);

            previousMonthButton.setOnClickListener(v -> handleErrors(this::previousWeekAction));
            nextMonthButton.setOnClickListener(v -> handleErrors(this::nextWeekAction));

            //start event edit
            Button bteventEdit = view.findViewById(R.id.btNewEvent);
            bteventEdit.setOnClickListener(v -> eventEdit());

        } catch (Exception e) {
            showErrorToast("An error occurred while loading the calendar.");
        }

        return view;
    }

    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.rvcalendar);
        monthYearText = view.findViewById(R.id.tvmonthYear);
        eventListView = view.findViewById(R.id.eventListView);
    }

    private void setWeekView() {
        try {
            if (CalendarUtils.selectedDate == null) {
                throw new IllegalStateException("Selected date is null");
            }

            monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
            ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

            if (days == null || days.isEmpty()) {
                throw new IllegalStateException("Days list is empty");
            }

            // Initialize adapter with this as the listener
            CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
            calendarRecyclerView.setLayoutManager(layoutManager);
            calendarRecyclerView.setAdapter(calendarAdapter);
            setEventAdapter();

            System.out.println("Adapter set with " + days.size() + " items");
        } catch (Exception e) {
            showErrorToast("Failed to update the calendar view. " + e.getMessage());
        }
    }

    private void previousWeekAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    private void nextWeekAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    private void eventEdit() {
        Intent intent = new Intent(getContext(), EventEdit.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    private void handleErrors(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            showErrorToast("An unexpected error occurred.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setEventAdapter();
    }

    private void setEventAdapter() {
        ArrayList<Event> dailyEvents = Event.eventsForDate(CalendarUtils.selectedDate);
        EventAdapter eventAdapter = new EventAdapter(getContext(), dailyEvents);
        eventListView.setAdapter(eventAdapter);
    }

    private void showErrorToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
