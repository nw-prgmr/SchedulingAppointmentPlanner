package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.antopina.schedulingappointmentplanner.HomePage.calendar.EventEdit;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.TaskEdit;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.adapter.TaskAdapter;
import com.antopina.schedulingappointmentplanner.databinding.FragmentTaskBinding;
import com.antopina.schedulingappointmentplanner.utils.EventDataBsaeHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class TaskFragment extends Fragment {

    FragmentTaskBinding binding;
    FirebaseAuth profileAuth;
    FirebaseFirestore fStore;

    private String userID, fullname, email;

    EventDataBsaeHelper eventDB;

    RecyclerView taskRecycler;
    ArrayList<String> event_id, event_title, event_description;
    TaskAdapter taskAdapter;

    private boolean isTaskFrameLayoutVisible = false;
    private boolean isEventFrameLayoutVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTaskBinding.inflate(inflater, container,false);
        View view = binding.getRoot();

        profileAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = profileAuth.getCurrentUser();
        userID = profileAuth.getCurrentUser().getUid();

        if (firebaseUser == null){
            Toast.makeText(getContext(), "Not Available", Toast.LENGTH_SHORT).show();
        } else {
            showUserProfile(firebaseUser);
        }

        taskRecycler = binding.taskRecycler;
        taskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));


        eventDB = new EventDataBsaeHelper(getContext());
        event_id = new ArrayList<>();
        event_title = new ArrayList<>();
        event_description = new ArrayList<>();

        storeDataInArray();

        TaskAdapter taskAdapter = new TaskAdapter(getContext(), event_id, event_title, event_description);
        taskRecycler.setAdapter(taskAdapter);

        binding.taskViewBT.setOnClickListener(v -> showTaskFrameLayout());
        binding.eventViewBT.setOnClickListener(v -> showEventFrameLayout());

        // Add scroll listener to RecyclerView
        binding.taskRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling down, hide the FAB
                    binding.floatingActionButton.hide();
                } else if (dy < 0) {
                    // Scrolling up, show the FAB
                    binding.floatingActionButton.show();
                }
            }
        });

        //FAB show bottomsheet
        binding.floatingActionButton.setOnClickListener(v -> showBottomDialog());

        return view;
    }

    private void showTaskFrameLayout() {
        if (isTaskFrameLayoutVisible) {
            // Hide the FrameLayout and change the button text to "View"
            binding.taskFL.setVisibility(View.GONE);
            binding.taskViewBT.setText("View");
            isTaskFrameLayoutVisible = false;
        } else {
            // Show the FrameLayout and change the button text to "Hide"
            binding.taskFL.setVisibility(View.VISIBLE);
            binding.taskViewBT.setText("Hide");

            // Check if the task list is empty and update noTaskTV visibility
            if (event_id.isEmpty()) {
                binding.noTaskTV.setVisibility(View.VISIBLE);
            } else {
                binding.noTaskTV.setVisibility(View.GONE);
            }

            isTaskFrameLayoutVisible = true;
        }
    }

    private void showEventFrameLayout() {
        if (isEventFrameLayoutVisible) {
            // Hide the FrameLayout and change the button text to "View"
            binding.eventFL.setVisibility(View.GONE);
            binding.eventViewBT.setText("View");
            isEventFrameLayoutVisible = false;
        } else {
            // Show the FrameLayout and change the button text to "Hide"
            binding.eventFL.setVisibility(View.VISIBLE);
            binding.eventViewBT.setText("Hide");

            // Check if the task list is empty and update noTaskTV visibility
//            if (event_id.isEmpty()) {
//                binding.noEventTV.setVisibility(View.VISIBLE);
//            } else {
//                binding.noEventTV.setVisibility(View.GONE);
//            }

            isEventFrameLayoutVisible = true;
        }
    }

    public void showBottomDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_bottomsheet);

        LinearLayout addTask = dialog.findViewById(R.id.layoutAddTask);
        LinearLayout addEvent = dialog.findViewById(R.id.layoutAddEvent);

        addTask.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), TaskEdit.class);
            startActivity(intent);

        });

        addEvent.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), EventEdit.class);
            startActivity(intent);
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void storeDataInArray() {
        Cursor cursor = eventDB.readAllData();
        if (cursor != null) {
            try {
                if (cursor.getCount() == 0) {
                    Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                } else {
                    while (cursor.moveToNext()) {
                        event_id.add(cursor.getString(0));
                        event_title.add(cursor.getString(1));
                        event_description.add(cursor.getString(2));
                    }
                }
            } catch (Exception e) {
                Log.e("TaskFragment", "Error reading database: " + e.getMessage());
                Toast.makeText(getContext(), "Error reading data.", Toast.LENGTH_SHORT).show();
            } finally {
                cursor.close();
            }
        } else {
            Log.e("TaskFragment", "Cursor is null.");
        }
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String fullName = document.getString("fName");
                    binding.customerTV.setText(fullName != null ? fullName : "User");
                } else {
                    Log.e("TaskFragment", "User document does not exist.");
                    Toast.makeText(getContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("TaskFragment", "Error fetching user profile: " + task.getException());
                Toast.makeText(getContext(), "Error fetching user profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}