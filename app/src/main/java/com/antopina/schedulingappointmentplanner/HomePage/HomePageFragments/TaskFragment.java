package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.adapter.TaskAdapter;
import com.antopina.schedulingappointmentplanner.databinding.FragmentTaskBinding;
import com.antopina.schedulingappointmentplanner.model.Task;

import java.util.ArrayList;
import java.util.List;


public class TaskFragment extends Fragment {

    FragmentTaskBinding binding;

    RecyclerView taskRecycler;
    TaskAdapter taskAdapter;
    List<Task> tasks = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTaskBinding.inflate(inflater, container,false);
        View view = binding.getRoot();

        return view;
    }



//    public void setUpAdapter() {
//        taskAdapter = new TaskAdapter(this, tasks, this);
//        taskRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        taskRecycler.setAdapter(taskAdapter);
//    }
}