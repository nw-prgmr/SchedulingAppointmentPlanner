package com.antopina.schedulingappointmentplanner.HomePage;

import static com.antopina.schedulingappointmentplanner.R.*;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.ActivityFragment;
import com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.CalendarFragment;
import com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.ProfileFragment;
import com.antopina.schedulingappointmentplanner.LoginSignup.Login;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.StudyFragment;
import com.antopina.schedulingappointmentplanner.databinding.ActivityHomePageViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomePageView extends AppCompatActivity {

    ActivityHomePageViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if the user is logged in and email is verified
        if (currentUser != null && !currentUser.isEmailVerified()) {
            // Sign out and redirect to Login
            mAuth.signOut();
            Intent intent = new Intent(HomePageView.this, Login.class);
            intent.putExtra("email_verification", "Your email is not verified. Please verify your email.");
            startActivity(intent);
            finish(); // Close HomePageView activity
            return;
        }

        String fragmentToOpen = getIntent().getStringExtra("open_fragment");
        if (fragmentToOpen != null) {
            if (fragmentToOpen.equals("activity_fragment")) {
                replaceFragment(new ActivityFragment()); // Open the EventFragment if specified
                return; // Skip setting up the bottom navigation
            }
        }

        // Check if "open_profile" flag is set in the intent
        if (getIntent().getBooleanExtra("open_profile", false)) {
            binding.bottomNavigationView.setSelectedItemId(id.bnvProfile);
            replaceFragment(new ProfileFragment()); // Open ProfileFragment directly
        } else {
            binding.bottomNavigationView.setSelectedItemId(id.bnvCalendar);
            replaceFragment(new CalendarFragment()); // Default fragment
        }


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == id.bnvTask) {
                replaceFragment(new ActivityFragment());
            } else if (item.getItemId() == R.id.bnvCalendar) {
                replaceFragment(new CalendarFragment());
            }  else if (item.getItemId() == R.id.bnvProfile) {
                replaceFragment(new ProfileFragment());
            }
//            else if (item.getItemId() == id.bnvStudy) {
//                replaceFragment(new StudyFragment());
//            }
//

            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);

        // Add the fragment to the back stack, so the back button can navigate through fragments
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }


}