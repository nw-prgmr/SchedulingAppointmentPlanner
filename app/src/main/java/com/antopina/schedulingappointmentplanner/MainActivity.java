package com.antopina.schedulingappointmentplanner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.antopina.schedulingappointmentplanner.HomePage.HomePageView;
import com.antopina.schedulingappointmentplanner.LoginSignup.EmailVerification;
import com.antopina.schedulingappointmentplanner.LoginSignup.SignupSeuccess;
import com.antopina.schedulingappointmentplanner.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (user.isEmailVerified()) {
                                // Email is verified, allow login and navigate to HomePage
                                Toast.makeText(MainActivity.this, "Email verified! You can now log in.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, SignupSeuccess.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Email is still not verified
                                Toast.makeText(MainActivity.this, "Email not verified yet. Please check your inbox.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

}