package com.antopina.schedulingappointmentplanner.LoginSignup;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.antopina.schedulingappointmentplanner.utils.LoadingDialogBar;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    ActivitySignupBinding binding;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    LoadingDialogBar loadingDialogBar;

    String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";

    private static final String TAG = "SignupActivity";
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        loadingDialogBar = new LoadingDialogBar(this);

        binding.etPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = 2; // Position for 'drawableEnd'
                if (event.getRawX() >= (binding.etPassword.getRight() - binding.etPassword.getCompoundDrawables()[drawableEnd].getBounds().width())) {
                    // Toggle password visibility
                    if (binding.etPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                        binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        binding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }
                    // Move the cursor to the end of the text
                    binding.etPassword.setSelection(binding.etPassword.getText().length());
                    return true;
                }
            }
            return false;
        });

        binding.etConfirmPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = 2; // Position for 'drawableEnd'
                if (event.getRawX() >= (binding.etConfirmPassword.getRight() - binding.etConfirmPassword.getCompoundDrawables()[drawableEnd].getBounds().width())) {
                    // Toggle password visibility
                    if (binding.etConfirmPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                        binding.etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        binding.etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }
                    // Move the cursor to the end of the text
                    binding.etConfirmPassword.setSelection(binding.etConfirmPassword.getText().length());
                    return true;
                }
            }
            return false;
        });


        binding.btSignup.setOnClickListener(view -> {
            try {
                String email = binding.etEmail.getText().toString().trim();
                String firstName = binding.etFirstName.getText().toString();
                String lastName = binding.etLastName.getText().toString();
                String password = binding.etPassword.getText().toString().trim();
                String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

                // Check for empty fields and set error messages
                if (TextUtils.isEmpty(email)) {
                    binding.etEmail.setError("Required Field");
                    binding.etEmail.requestFocus();
                    return;
                }else if (TextUtils.isEmpty(firstName)) {
                    binding.etFirstName.setError("Required Field");
                    binding.etFirstName.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(lastName)) {
                    binding.etLastName.setError("Required Field");
                    binding.etLastName.requestFocus();
                    return;
                }else if (TextUtils.isEmpty(password)) {
                    binding.etPassword.setError("Required Field");
                    binding.etPassword.requestFocus();
                    return;
                }else if (TextUtils.isEmpty(confirmPassword)) {
                    binding.etConfirmPassword.setError("Required Field");
                    binding.etConfirmPassword.requestFocus();
                    return;
                }

                if (lastName.length() < 4 ) {
                    binding.etLastName.setError("It must be 4 characters long");
                    binding.etLastName.requestFocus();
                    return;
                }

                // Validate password
                if (!password.matches(passwordPattern)) {
                    binding.etPassword.setError("Password must be at least 6 characters long, include an uppercase letter, a lowercase letter, a number, and a special character.");
                    binding.etPassword.requestFocus();
                    return;
                }

                // Confirm that passwords match
                if (password.equals(confirmPassword)) {
                    loadingDialogBar.ShowDialog("Creating account...");

                    // Create the user
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // User created, now send a verification email
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(Signup.this, "Verification email sent. Please check your email.", Toast.LENGTH_LONG).show();

                                                        // Save user details to Firestore
                                                        userID = mAuth.getCurrentUser().getUid();
                                                        DocumentReference documentReference = fstore.collection("users").document(userID);
                                                        Map<String, Object> users = new HashMap<>();
                                                        users.put("firstName", firstName);
                                                        users.put("lastName", lastName);
                                                        users.put("email", email);
                                                        documentReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Log.d(TAG, "OnSuccess: user profile is created for " + userID);
                                                            }
                                                        });

                                                        // Redirect to MainActivity
                                                        showDialog();
                                                    } else {
                                                        Toast.makeText(Signup.this, "Failed to send verification email. Please try again.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                        loadingDialogBar.HideDialog();
                                    } else {
                                        // Handle exceptions
                                        Toast.makeText(Signup.this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
                                        loadingDialogBar.HideDialog();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Redirect to the login screen instead of exiting or signing in
        Intent intent = new Intent(Signup.this, Login.class);
        startActivity(intent);
        finish(); // Ensure Signup activity is closed
    }

    private void showDialog() {
        // Create a dialog to inform the user
        Dialog dialog = new Dialog(Signup.this);
        dialog.setContentView(R.layout.dialog_email_signup); // Reference the layout XML file
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}