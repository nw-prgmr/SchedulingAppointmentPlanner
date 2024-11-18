package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.antopina.schedulingappointmentplanner.HomePage.HomePageView;
import com.antopina.schedulingappointmentplanner.HomePage.Settings;
import com.antopina.schedulingappointmentplanner.LoginSignup.ReadWriteUserDetails;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Document;

import java.util.concurrent.Executor;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth profileAuth;
    FirebaseFirestore fStore;

    private String userID, fullname, email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        View view =  binding.getRoot();

        profileAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = profileAuth.getCurrentUser();
        userID = profileAuth.getCurrentUser().getUid();


        binding.ivSettings.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), Settings.class);
            startActivity(intent);
        });


        if (firebaseUser == null){
            Toast.makeText(getContext(), "Not Available", Toast.LENGTH_SHORT).show();
        } else {
            showUserProfile(firebaseUser);
        }

        return view;
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Retrieve fields from Firestore and display them
                    String fullName = document.getString("fName");
                    String email = document.getString("email");

                    binding.tvFullName.setText(fullName);
                    binding.tvEmail.setText(email);
                } else {
                    Toast.makeText(getContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}