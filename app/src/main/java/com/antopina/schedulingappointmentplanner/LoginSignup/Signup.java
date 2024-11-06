package com.antopina.schedulingappointmentplanner.LoginSignup;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.antopina.schedulingappointmentplanner.HomePage.HomePageView;
import com.antopina.schedulingappointmentplanner.MainActivity;
import com.antopina.schedulingappointmentplanner.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signup extends AppCompatActivity {

    ActivitySignupBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();



        binding.btSignup.setOnClickListener(view -> {
            try {
                String email = binding.etEmail.getText().toString().trim();
                String fullName = binding.etFullName.getText().toString();
                String password = binding.etPassword.getText().toString().trim();
                String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

                // Check for empty fields and set error messages
                if (TextUtils.isEmpty(email)) {
                    binding.etEmail.setError("Required Field");
                    binding.etEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(fullName)) {
                    binding.etFullName.setError("Required Field");
                    binding.etFullName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    binding.etPassword.setError("Required Field");
                    binding.etPassword.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    binding.etConfirmPassword.setError("Required Field");
                    binding.etConfirmPassword.requestFocus();
                    return;
                }

                // Validate password length
                if (password.length() < 8 || confirmPassword.length() < 8) {
                    binding.etPassword.setError("It must be 8 characters long");
                    binding.etConfirmPassword.setError("It must be 8 characters long");
                    return;
                }

                // Confirm that passwords match
                if (password.equals(confirmPassword)) {
                    binding.progressBar.setVisibility(View.VISIBLE);

                    // Create the user
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    binding.progressBar.setVisibility(View.GONE);

                                    if (task.isSuccessful()) {
                                        // User created, now send a verification email
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(Signup.this, "Verification email sent. Please check your email.", Toast.LENGTH_LONG).show();

                                                        // Redirect to EmailVerificationActivity
                                                        startActivity(new Intent(Signup.this, EmailVerification.class));
                                                    } else {
                                                        Toast.makeText(Signup.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        Toast.makeText(Signup.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    binding.etConfirmPassword.setError("Passwords do not match");
                    binding.etConfirmPassword.requestFocus();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        binding.tvLogin.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        });

    }
}