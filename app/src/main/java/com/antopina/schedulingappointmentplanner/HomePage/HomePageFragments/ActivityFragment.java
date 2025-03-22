package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.ActivityFragments.EventFragment;
import com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.ActivityFragments.TaskFragment;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.databinding.FragmentActivityBinding;
import com.google.android.material.tabs.TabLayout;


public class ActivityFragment extends Fragment {

    FragmentActivityBinding binding;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActivityBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize TabLayout
        tabLayout = view.findViewById(R.id.activityTL);

        // Set default fragment
        replaceFragment(new TaskFragment());

        // Add tabs to TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Task"));
        tabLayout.addTab(tabLayout.newTab().setText("Events"));

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    replaceFragment(new TaskFragment());
                } else if (position == 1) {
                    replaceFragment(new EventFragment());
                }
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

        binding.refreshTV.setOnClickListener(v -> refreshFragment());



        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Ensure the ID of the FrameLayout is correct
        fragmentTransaction.replace(R.id.activity_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void refreshFragment() {
        ActivityFragment activityFragment = new ActivityFragment();  // Create a new instance of TaskFragment
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, activityFragment)  // Replace with new TaskFragment instance
                .addToBackStack(null)  // Optional: add to back stack
                .commit();
    }
}