package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.CalendarFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antopina.schedulingappointmentplanner.R;

public class DailyView extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_view, container, false);
    }
}