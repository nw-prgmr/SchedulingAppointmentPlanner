package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.antopina.schedulingappointmentplanner.HomePage.HomePageView;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.databinding.FragmentStudyBinding;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;


public class StudyFragment extends Fragment {

    FragmentStudyBinding binding;

    private int timeSelected = 0;
    private CountDownTimer timeCountDown = null;
    private int timeProgress = 0;
    private long pauseOffSet = 0;
    private boolean isStart = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStudyBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnAdd.setOnClickListener(view1 -> setTimeFunction());

        binding.btnPlayPause.setOnClickListener(view1 -> startTimerSetup());

        return view;
    }

    private void resetTime() {
        if (timeCountDown != null) {
            timeCountDown.cancel();
            timeProgress = 0;
            timeSelected = 0;
            pauseOffSet = 0;
            timeCountDown = null;

            Button startBtn = binding.btnPlayPause;
            startBtn.setText("Start");
            isStart = true;

            ProgressBar progressBar = binding.pbTimer;
            progressBar.setProgress(0);

            TextView timeLeftTv = binding.tvTimeLeft;
            timeLeftTv.setText("0");
        }
    }

    private void timePause() {
        if (timeCountDown != null) {
            timeCountDown.cancel();
        }
    }
    private void startTimerSetup() {
        Button startBtn = binding.btnPlayPause;
        if (timeSelected > timeProgress) {
            if (isStart) {
                startBtn.setText("Pause");
                startTimer(pauseOffSet);
                isStart = false;
            } else {
                isStart = true;
                startBtn.setText("Resume");
                timePause();
            }
        } else {
            Toast.makeText(getContext(), "Enter Time", Toast.LENGTH_SHORT).show();
        }
    }


    private void startTimer(long pauseOffSetL) {
        ProgressBar progressBar = binding.pbTimer;
        progressBar.setProgress(timeProgress);

        timeCountDown = new CountDownTimer((timeSelected * 1000L) - (pauseOffSetL * 1000), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeProgress++;
                pauseOffSet = timeSelected - millisUntilFinished / 1000;
                progressBar.setProgress(timeSelected - timeProgress);

                TextView timeLeftTv = binding.tvTimeLeft;
                timeLeftTv.setText(String.valueOf(timeSelected - timeProgress));
            }

            @Override
            public void onFinish() {
                resetTime();
                Toast.makeText(getContext(), "Times Up!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void setTimeFunction() {
        resetTime(); // Reset any previous timer
        timeSelected = 25; // Set a fixed duration of 25 seconds
        ProgressBar progressBar = binding.pbTimer;
        TextView timeLeftTv = binding.tvTimeLeft;
        Button btnStart = binding.btnPlayPause;

        // Update the display with the fixed time
        timeLeftTv.setText(String.valueOf(timeSelected));
        btnStart.setText("Start");
        progressBar.setMax(timeSelected);
    }
}