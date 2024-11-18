package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.CalendarFragments;

import static com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils.daysInMonthArray;
import static com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils.monthYearFromDate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.antopina.schedulingappointmentplanner.HomePage.adapter.CalendarAdapter;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils;
import com.antopina.schedulingappointmentplanner.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class MonthlyView extends Fragment implements CalendarAdapter.OnItemListener {


    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_view, container, false);

        try {
            // Initialize widgets
            initWidgets(view);

            // Set initial date
            CalendarUtils.selectedDate = LocalDate.now();

            // Setup the calendar view
            setMonthView();

            // Set click listeners for buttons
            Button previousMonthButton = view.findViewById(R.id.btnPreviousMonth);
            Button nextMonthButton = view.findViewById(R.id.btnNextMonth);

            previousMonthButton.setOnClickListener(v -> handleErrors(this::previousMonthAction));
            nextMonthButton.setOnClickListener(v -> handleErrors(this::nextMonthAction));
        } catch (Exception e) {
            showErrorToast("An error occurred while loading the calendar.");
        }

        return view;
    }

    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.rvcalendar);
        monthYearText = view.findViewById(R.id.tvmonthYear);
    }

    private void setMonthView() {
        try {
            monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
            ArrayList<LocalDate> daysInMonth = daysInMonthArray();

            // Initialize adapter with this as the listener
            CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
            calendarRecyclerView.setLayoutManager(layoutManager);
            calendarRecyclerView.setAdapter(calendarAdapter);
        } catch (Exception e) {
            showErrorToast("Failed to update the calendar view.");
        }
    }



    private void previousMonthAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    private void nextMonthAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
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