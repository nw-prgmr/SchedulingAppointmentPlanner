package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.antopina.schedulingappointmentplanner.R;

public class InfoDialog extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content";

    public static InfoDialog newInstance(String title, String content) {
        InfoDialog dialog = new InfoDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_info, container, false);

        // Retrieve title and content
        String title = getArguments().getString(ARG_TITLE);
        String content = getArguments().getString(ARG_CONTENT);

        // Set dialog content
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvContent = view.findViewById(R.id.tvContent);

        tvTitle.setText(title);
        tvContent.setText(content);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public int getTheme() {
        return R.style.CustomDialogTheme; // Optional: Define a custom theme in `styles.xml`
    }
}
