package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.antopina.schedulingappointmentplanner.HomePage.Settings;
import com.antopina.schedulingappointmentplanner.LoginSignup.Login;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.databinding.FragmentProfileBinding;
import com.antopina.schedulingappointmentplanner.model.Task;
import com.antopina.schedulingappointmentplanner.utils.AppConstants;
import com.antopina.schedulingappointmentplanner.utils.EventDataBsaeHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth profileAuth;
    FirebaseFirestore fStore;

    private String userID;
    private EventDataBsaeHelper dbHelper; // Your database helper class for SQLite

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        View view =  binding.getRoot();

        // Initialize Firebase Auth and Firestore
        profileAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Initialize SQLite DBHelper
        dbHelper = new EventDataBsaeHelper(getContext());

        // Get the current Firebase user
        FirebaseUser firebaseUser = profileAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(getContext(), "Not Available", Toast.LENGTH_SHORT).show();
        } else {
            userID = firebaseUser.getUid();
            showUserProfile();
        }

        // Fetch task counts
        fetchTaskCounts();

        // Navigate to Settings activity
//        binding.settingsIcon.setOnClickListener(view1 -> {
//            Intent intent = new Intent(getContext(), Settings.class);
//            startActivity(intent);
//        });

        setupCardListeners();

        binding.logout.setOnClickListener(v -> logout());

        return view;
    }

    // Show user profile by fetching data from Firestore
    private void showUserProfile() {
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");
                String email = documentSnapshot.getString("email");

                // Update UI with retrieved data
                binding.tvFullName.setText(firstName + " " + lastName);
                binding.tvEmail.setText(email);
            } else {
                Toast.makeText(getContext(), "User does not exist", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error retrieving user data", Toast.LENGTH_SHORT).show();
        });
    }

    // Fetch the counts of completed and pending tasks from SQLite database
    private void fetchTaskCounts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query to count completed tasks (status = 1)
        String completedQuery = "SELECT COUNT(*) FROM task WHERE task_status = 1";
        Cursor completedCursor = db.rawQuery(completedQuery, null);
        if (completedCursor.moveToFirst()) {
            int completedTasks = completedCursor.getInt(0);
            binding.completedTaskTV.setText(String.valueOf(completedTasks)); // Set the completed tasks count
        }
        completedCursor.close();

        // Query to count pending tasks (status = 0)
        String pendingQuery = "SELECT COUNT(*) FROM task WHERE task_status = 0";
        Cursor pendingCursor = db.rawQuery(pendingQuery, null);
        if (pendingCursor.moveToFirst()) {
            int pendingTasks = pendingCursor.getInt(0);
            binding.pendingTaskTV.setText(String.valueOf(pendingTasks)); // Set the pending tasks count
        }
        pendingCursor.close();

        db.close();
    }

    private void showDialog(String title, String content) {
        InfoDialog dialog = InfoDialog.newInstance(title, content);
        dialog.show(getParentFragmentManager(), "InfoDialog"); // Use getSupportFragmentManager in an activity
    }

    private void setupCardListeners() {
        // Example in Manage Profile card
        View manageProfileCard = binding.cardManageProfile.getRoot();
        TextView manageTitle = manageProfileCard.findViewById(R.id.cardTitle);
        ImageView manageIcon = manageProfileCard.findViewById(R.id.cardIcon);
        ImageButton manageNext = manageProfileCard.findViewById(R.id.cardNextIcon);

        manageTitle.setText("Manage Account");
        manageIcon.setImageResource(R.drawable.person);

        // Set click listener
        manageNext.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Settings.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


        // Example in Privacy Policy card
        View cardPrivacyPolicy = binding.cardPrivacyPolicy.getRoot();
        TextView privacyTitle = cardPrivacyPolicy.findViewById(R.id.cardTitle);
        ImageView privacyIcon = cardPrivacyPolicy.findViewById(R.id.cardIcon);
        ImageButton privacyNext = cardPrivacyPolicy.findViewById(R.id.cardNextIcon);

        privacyTitle.setText("Privacy Policy");
        privacyIcon.setImageResource(R.drawable.ic_policy);

        // Set click listener
        privacyNext.setOnClickListener(v -> {
            showDialog("Privacy Policy", AppConstants.PRIVACY_POLICY_CONTENT);
        });


        // Example in Terms and Condition card
        View cardTermsAndConditions = binding.cardTermsAndConditions.getRoot();
        TextView termsTitle = cardTermsAndConditions.findViewById(R.id.cardTitle);
        ImageView termsIcon = cardTermsAndConditions.findViewById(R.id.cardIcon);
        ImageButton termsNext = cardTermsAndConditions.findViewById(R.id.cardNextIcon);

        termsTitle.setText("Terms and Conditions");
        termsIcon.setImageResource(R.drawable.ic_article);

        // Set click listener
        termsNext.setOnClickListener(v -> {
            showDialog("Terms and Conditions", AppConstants.TERMS_CONDITIONS_CONTENT);
        });

        // Example in About card
        View cardAbout = binding.cardAbout.getRoot();
        TextView aboutTitle = cardAbout.findViewById(R.id.cardTitle);
        ImageView aboutIcon = cardAbout.findViewById(R.id.cardIcon);
        ImageButton aboutNext = cardAbout   .findViewById(R.id.cardNextIcon);

        aboutTitle.setText("About");
        aboutIcon.setImageResource(R.drawable.ic_info);

        // Set click listener
        aboutNext.setOnClickListener(v -> {
            showDialog("About", AppConstants.ABOUT_CONTENT);
        });


        // Example in Terms and Condition card
        View completedTask = binding.completedTask.getRoot();
        TextView completeTaskTitle = completedTask.findViewById(R.id.cardTitle);
        ImageView completeIcon = completedTask.findViewById(R.id.cardIcon);
        ImageButton completeNext = completedTask.findViewById(R.id.cardNextIcon);


        completeTaskTitle.setText("Completed Task");
        completeIcon.setImageResource(R.drawable.ic_task);

        // Set click listener
        completeNext.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CompletedTask.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    public void logout() {
        // Create a new Dialog
        Dialog dialog = new Dialog(getContext());

        // Set the custom layout
        dialog.setContentView(R.layout.alert_dialog);

        // Find the buttons and other views in the layout
        AppCompatButton btnYes = dialog.findViewById(R.id.btn_yes);
        AppCompatButton btnNo = dialog.findViewById(R.id.btn_no);
        TextView alertMessage = dialog.findViewById(R.id.alert_message);
        TextView alertTitle = dialog.findViewById(R.id.alert_task_title);

        // Set dynamic text
        alertTitle.setText("Logout.");
        alertMessage.setText("Are you sure you want to log out?");

        // Handle Yes button click (Perform logout)
        btnYes.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            dialog.dismiss(); // Close the dialog after logout
        });

        // Handle No button click (Dismiss the dialog)
        btnNo.setOnClickListener(v -> dialog.dismiss());

        // Display the dialog
        dialog.show();
    }


}
