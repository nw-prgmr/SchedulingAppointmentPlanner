package com.antopina.schedulingappointmentplanner.LoginSignup;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.databinding.ActivitySignupSeuccessBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignupSeuccess extends AppCompatActivity {

    ActivitySignupSeuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignupSeuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btLogin.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

    }
}