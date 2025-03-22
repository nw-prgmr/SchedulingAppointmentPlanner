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
import com.antopina.schedulingappointmentplanner.databinding.FragmentActivityBinding;
import com.google.android.material.tabs.TabLayout;


public class CalendarFragment extends Fragment{


    private Button btWeekly, btMonthly, btDaily;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);


        // Initialize TabLayout
        tabLayout = view.findViewById(R.id.tab_layout);

        // Set default fragment
        replaceFragment(new MonthlyView());

        // Add tabs to TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Monthly"));
        tabLayout.addTab(tabLayout.newTab().setText("Weekly"));
//        tabLayout.addTab(tabLayout.newTab().setText("Daily"));

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    replaceFragment(new MonthlyView());
                } else if (position == 1) {
                    replaceFragment(new WeekView());
                }
//                else if (position == 2) {
//                    replaceFragment(new DailyView());
//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });



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