package com.antopina.schedulingappointmentplanner.HomePage;

import static com.antopina.schedulingappointmentplanner.R.*;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.CalendarFragment;
import com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.ProfileFragment;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.StudyFragment;
import com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.TaskFragment;
import com.antopina.schedulingappointmentplanner.databinding.ActivityHomePageViewBinding;

public class HomePageView extends AppCompatActivity {

    ActivityHomePageViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        // Check if "open_profile" flag is set in the intent
        if (getIntent().getBooleanExtra("open_profile", false)) {
            binding.bottomNavigationView.setSelectedItemId(R.id.bnvProfile);
            replaceFragment(new ProfileFragment());
        } else {
            binding.bottomNavigationView.setSelectedItemId(R.id.bnvCalendar);
            replaceFragment(new CalendarFragment());
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == id.bnvTask) {
                replaceFragment(new TaskFragment());
            } else if (item.getItemId() == R.id.bnvCalendar) {
                replaceFragment(new CalendarFragment());
            } else if (item.getItemId() == R.id.bnvStudy) {
                replaceFragment(new StudyFragment());
            } else if (item.getItemId() == R.id.bnvProfile) {
                replaceFragment(new ProfileFragment());
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