package com.antopina.schedulingappointmentplanner.HomePage;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.antopina.schedulingappointmentplanner.LoginSignup.Login;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseFirestore firestore;
    FirebaseUser currentUser;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Firestore and Authentication
        firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Get the current logged-in user
        currentUser = auth.getCurrentUser();

        // Ensure the user is logged in
        if (currentUser != null) {
            userId = currentUser.getUid(); // Get the current user's ID
        } else {
            // If the user is not logged in, redirect them to login or show an error
            Toast.makeText(Settings.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Back button click listener
        binding.backButton.setOnClickListener(v -> navigateToHomePageWithProfile());

        // Set EditText fields non-editable by default
        setFieldsEditable(false);

        // Fetch and display user profile data from Firestore
        fetchUserProfileData();

        // Manage Profile icon click listener
        binding.manageProfileIV.setOnClickListener(v -> {
            // Show the update button and hide the manage profile icon
            binding.manageProfileIV.setVisibility(View.GONE);
            binding.updateProfileBT.setVisibility(View.VISIBLE);
            binding.deleteAccountBT.setVisibility(View.VISIBLE);

            // Make the fields editable
            setFieldsEditable(true);
        });

        // Update Profile button click listener
        binding.updateProfileBT.setOnClickListener(v -> updateProfile());

        binding.deleteAccountBT.setOnClickListener(v -> showDeleteAccountConfirmationDialog());
    }

    // Set EditText fields to be editable or not
    private void setFieldsEditable(boolean editable) {
        binding.firstNameUpdate.setEnabled(editable);
        binding.lastNameUpdate.setEnabled(editable);
    }

    // Fetch user profile data from Firestore
    private void fetchUserProfileData() {
        if (userId != null) {
            DocumentReference userDocRef = firestore.collection("users").document(userId);

            userDocRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("firstName");
                            String lastName = documentSnapshot.getString("lastName");
                            String email = documentSnapshot.getString("email");

                            // Set the fetched data into the EditTexts
                            binding.firstNameUpdate.setText(firstName);
                            binding.lastNameUpdate.setText(lastName);
                            binding.emailTV.setText(email);
                        } else {
                            Toast.makeText(Settings.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Settings.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Update the user profile in Firestore and Firebase Authentication
    private void updateProfile() {
        String updatedFirstName = binding.firstNameUpdate.getText().toString();
        String updatedLastName = binding.lastNameUpdate.getText().toString();

        if (TextUtils.isEmpty(updatedFirstName)) {
            binding.firstNameUpdate.setError("Required Field");
            binding.firstNameUpdate.requestFocus();
            return;
        } else if (TextUtils.isEmpty(updatedLastName)) {
            binding.lastNameUpdate.setError("Required Field");
            binding.lastNameUpdate.requestFocus();
            return;
        }

        if (updatedLastName.length() < 4 ) {
            binding.lastNameUpdate.setError("It must be 4 characters long");
            binding.lastNameUpdate.requestFocus();
            return;
        }

        // Create a map with updated data for Firestore
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("firstName", updatedFirstName);
        updatedData.put("lastName", updatedLastName);

        if (userId != null) {
            // Update Firestore data (but not the email)
            firestore.collection("users").document(userId)
                    .update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Settings.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        // After updating, set fields to non-editable and hide update button
                        setFieldsEditable(false);
                        binding.updateProfileBT.setVisibility(View.GONE);
                        binding.deleteAccountBT.setVisibility(View.GONE);
                        binding.manageProfileIV.setVisibility(View.VISIBLE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Settings.this, "Failed to update profile in Firestore", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void showDeleteAccountConfirmationDialog() {
        // Inflate the custom alert dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog, null);

        TextView taskTitleTextView = dialogView.findViewById(R.id.alert_task_title);
        TextView taskDescriptionTextView = dialogView.findViewById(R.id.alert_message);
        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        Button btnNo = dialogView.findViewById(R.id.btn_no);

        // Set the title and description for the dialog
        taskTitleTextView.setText("Delete Account?");
        taskDescriptionTextView.setText("Are you sure you want to delete your account? This action cannot be undone.");

        // Create and configure the dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogView);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Handle "Yes" button click
        btnYes.setOnClickListener(view -> {
            deleteAccount(); // Call the deletion method
            dialog.dismiss();
        });

        // Handle "No" button click
        btnNo.setOnClickListener(view -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }

    private void deleteAccount() {
        // First, delete the user from Firestore
        if (userId != null) {
            firestore.collection("users").document(userId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Then, delete the user from Firebase Authentication
                        currentUser.delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Settings.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                        // Redirect to login screen or home screen after account deletion
                                        Intent intent = new Intent(Settings.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(Settings.this, "Failed to delete account from Firebase Authentication", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Settings.this, "Failed to delete account from Firestore", Toast.LENGTH_SHORT).show();
                    });
        }
    }



    // Navigate to HomePage with ProfileFragment when the back button is clicked
    private void navigateToHomePageWithProfile() {
        Intent intent = new Intent(this, HomePageView.class);
        intent.putExtra("open_profile", true); // Flag to open ProfileFragment
        startActivity(intent);
        finish();
    }
}
