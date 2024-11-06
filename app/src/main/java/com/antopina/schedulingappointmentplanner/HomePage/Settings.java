package com.antopina.schedulingappointmentplanner.HomePage;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.antopina.schedulingappointmentplanner.LoginSignup.Login;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {

    ActivitySettingsBinding binding;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Set up the action bar with a back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.burgundy))); // Use getSupportActionBar()
        }

        binding.btLogout.setOnClickListener(view -> logout());
    }

    //    logout
    public void logout() {
        FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
        // Redirect to the login screen or initial activity
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the back button in the action bar
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, HomePageView.class);
            intent.putExtra("open_profile", true); // Flag to open ProfileFragment
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
