package com.antopina.schedulingappointmentplanner.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.antopina.schedulingappointmentplanner.HomePage.calendar.EventEdit;
import com.antopina.schedulingappointmentplanner.HomePage.calendar.TaskEdit;
import com.antopina.schedulingappointmentplanner.R;

public class BottomDialogHelper {

    public static void showBottomDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_bottomsheet);

        LinearLayout addTask = dialog.findViewById(R.id.layoutAddTask);
        LinearLayout addEvent = dialog.findViewById(R.id.layoutAddEvent);

        addTask.setOnClickListener(view -> {
            Intent intent = new Intent(context, TaskEdit.class);
            context.startActivity(intent);
        });

        addEvent.setOnClickListener(view -> {
            Intent intent = new Intent(context, EventEdit.class);
            context.startActivity(intent);
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
