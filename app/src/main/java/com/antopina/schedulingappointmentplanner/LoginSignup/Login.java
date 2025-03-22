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
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.antopina.schedulingappointmentplanner.HomePage.HomePageView;
import com.antopina.schedulingappointmentplanner.utils.LoadingDialogBar;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth mAuth;
    LoadingDialogBar loadingDialogBar;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
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

        binding.btLogin.setOnClickListener(view -> {
            try {
                String email = binding.etEmail.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();

                // Set error for empty fields
                if (TextUtils.isEmpty(email)) {
                    binding.etEmail.setError("Required Field");
                    binding.etEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    binding.etPassword.setError("Required Field");
                    binding.etPassword.requestFocus();
                    return;
                }

                if (password.length() < 6 ) {
                    binding.etPassword.setError("It must be 6 characters long");
                    binding.etPassword.requestFocus();
                    return;
                }

                /// Show the loading dialog
                loadingDialogBar.ShowDialog("Logging in...");

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Hide the progress bar when the task completes
                        loadingDialogBar.HideDialog();

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), HomePageView.class);
                                // Prevent going back to the Login screen
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                if (user != null) {
                                    // Show message if the email is not verified
                                    Toast.makeText(Login.this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut(); // Sign out to prevent the user from proceeding
                                }
                            }
                        } else {
                            // Handle other login errors
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                binding.etEmail.setError("User does not exist or is no longer valid.");
                                binding.etEmail.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                binding.etEmail.setError("Invalid credentials.");
                                binding.etEmail.requestFocus();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            } catch (Exception e) {
                loadingDialogBar.HideDialog();
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvForgot.setOnClickListener(view -> {
            showDialog();
        });

        binding.tvSignup.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Signup.class);
            startActivity(intent);
        });

    }

    //check if user is already logged in.
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomePageView.class));
            finish();
        }
    }

    private void showDialog() {
        // Create a dialog to inform the user
        Dialog dialog = new Dialog(Login.this);
        dialog.setContentView(R.layout.dialog_reset_password); // Reference the layout XML file
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set click listener for the button
        Button resetButton = dialog.findViewById(R.id.btReset);
        resetButton.setOnClickListener(view -> {
            String email = ((TextInputEditText) dialog.findViewById(R.id.etEmail)).getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                sendPasswordResetEmail(email);
//                dialog.dismiss(); // Close dialog after sending the email
            } else {
                // Handle empty email field
                Toast.makeText(Login.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void sendPasswordResetEmail(String email) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Reset email sent, please check your inbox.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Login.this, "Failed to send reset email. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}