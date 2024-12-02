package com.antopina.schedulingappointmentplanner.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments.TaskFragment;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.model.TodoModel;
import com.antopina.schedulingappointmentplanner.utils.DataBaseHelper;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.MyViewHolder> {

    private List<TodoModel> mList;
    private TaskFragment taskFragment;
    private DataBaseHelper myDB;

    public TodoAdapter(DataBaseHelper myDB, TaskFragment taskFragment){
        this.taskFragment = taskFragment;
        this.myDB = myDB;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final TodoModel item = mList.get(position);
    }

    public boolean toBoolean(int num) {
        return num != 0;
    }

    public TaskFragment getContext(){
        return taskFragment;
    }

    public void setTask (List<TodoModel> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public void deleteTask(int position){
        TodoModel item = mList.get(position);
        myDB.deleteTask(item.getId());
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        TodoModel item = mList.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt("id" , item.getId());
        bundle.putString("task" , item.getTask());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
