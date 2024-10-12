package com.antopina.schedulingappointmentplanner;

import static com.antopina.schedulingappointmentplanner.R.*;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.antopina.schedulingappointmentplanner.databinding.ActivityHomePageViewBinding;

public class HomePageView extends AppCompatActivity {

    ActivityHomePageViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setSelectedItemId(R.id.bnvCalendar);
        replaceFragment(new CalendarFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == id.bnvTask) {
                replaceFragment(new TaskFragment());
            } else if (item.getItemId() == R.id.bnvCalendar) {
                replaceFragment(new CalendarFragment());
            } else if (item.getItemId() == R.id.bnvStudy) {
                replaceFragment(new StudyFragment());
            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}