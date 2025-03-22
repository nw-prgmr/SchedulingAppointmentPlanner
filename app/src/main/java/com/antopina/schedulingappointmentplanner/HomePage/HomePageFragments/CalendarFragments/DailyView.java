package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.CalendarFragments;

import static com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils.selectedDate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.antopina.schedulingappointmentplanner.HomePage.calendar.Event;
import com.antopina.schedulingappointmentplanner.adapter.HourAdapter;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.CalendarUtils;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.EventEdit;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.HourEvent;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.databinding.FragmentDailyViewBinding;
import com.antopina.schedulingappointmentplanner.utils.BottomDialogHelper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class DailyView extends Fragment {

    private FragmentDailyViewBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Use view binding to inflate the layout
        binding = FragmentDailyViewBinding.inflate(inflater, container, false);

        // Ensure selectedDate is initialized
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }

        // Initialize and set up UI elements
        initWidgets();
        setDayView();
        setListeners();

        return binding.getRoot();
    }

    private void initWidgets() {
        // Widgets are already linked using binding
    }

    private void setListeners() {
        // Set click listeners for buttons
        binding.btnPreviousDaily.setOnClickListener(v -> handleErrors(this::previousDailyAction));
        binding.btnNextDaily.setOnClickListener(v -> handleErrors(this::nextDailyAction));

        // Set scroll listener on the ListView
        binding.hourListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Optional: Handle scroll state changes
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getLastVisiblePosition() == totalItemCount - 1 && view.getChildAt(view.getChildCount() - 1) != null &&
                        view.getChildAt(view.getChildCount() - 1).getBottom() <= view.getHeight()) {
                    // Scrolling down (list reached the bottom)
                    binding.floatingActionButton.hide();
                } else if (view.getFirstVisiblePosition() == 0 && view.getChildAt(0) != null &&
                        view.getChildAt(0).getTop() >= 0) {
                    // Scrolling up (list reached the top)
                    binding.floatingActionButton.show();
                }
            }
        });

        // Set click listener for FloatingActionButton
        binding.floatingActionButton.setOnClickListener(v -> BottomDialogHelper.showBottomDialog(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        setDayView();
    }

    private void setDayView() {
        binding.monthDayText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        binding.tvDayOfWeek.setText(dayOfWeek);
        setHourAdapter();
    }

    private void setHourAdapter() {
        HourAdapter hourAdapter = new HourAdapter(getContext(), hourEventList());
        binding.hourListView.setAdapter(hourAdapter);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
