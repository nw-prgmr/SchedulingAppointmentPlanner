package com.antopina.schedulingappointmentplanner.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.antopina.schedulingappointmentplanner.R;

public class LoadingDialogBar {

    Context context;
    Dialog dialog;

    public LoadingDialogBar(Context context) {
        this.context = context;
    }

    public void ShowDialog(String title) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView loadingTV = dialog.findViewById(R.id.loadingProgressBarTV);

        loadingTV.setText(title);
        dialog.create();
        dialog.show();
    }

    public void HideDialog() {
        dialog.dismiss();
    }
 }
