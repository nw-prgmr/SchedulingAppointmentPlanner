package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.CalendarFragments.DailyView;
import com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.CalendarFragments.MonthlyView;
import com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.CalendarFragments.WeekView;
import com.antopina.schedulingappointmentplanner.R;


public class CalendarFragment extends Fragment{

    private Button btWeekly, btMonthly, btDaily;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Initialize buttons
        btWeekly = view.findViewById(R.id.btWeekly);
        btMonthly = view.findViewById(R.id.btMonthly);
        btDaily = view.findViewById(R.id.btDailyly);

        // Call the method to replace the fragment into the FrameLayout
        replaceFragment(new MonthlyView());
        btWeekly.setOnClickListener(v -> replaceFragment(new WeekView()));
        btMonthly.setOnClickListener(v -> replaceFragment(new MonthlyView()));
        btDaily.setOnClickListener(v -> replaceFragment(new DailyView()));

        return view;

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Ensure the ID of the FrameLayout is correct
        fragmentTransaction.replace(R.id.calendar_frame_layout, fragment);
        fragmentTransaction.commit();
    }
}