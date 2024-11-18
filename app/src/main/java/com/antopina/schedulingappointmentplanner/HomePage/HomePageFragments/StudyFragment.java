package com.antopina.schedulingappointmentplanner.HomePage.HomePageFragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.databinding.FragmentStudyBinding;

public class StudyFragment extends Fragment {

    FragmentStudyBinding binding;
    private int timeSelected = 0; // Total time in seconds
    private CountDownTimer timeCountDown = null;
    private int timeProgress = 0;
    private long pauseOffSet = 0;
    private boolean isStart = true;
    private static final String CHANNEL_ID = "POMODORO_TIMER_CHANNEL";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStudyBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        createNotificationChannel();
        setWorkingTime(); // Initialize the timer to 25 minutes when the fragment is created

        binding.btnStart.setOnClickListener(view1 -> startTimerSetup());

        // Pause/Play button toggle
        binding.ivPausePlay.setOnClickListener(view1 -> startTimerSetup());

        // Stop button to reset the timer
        binding.ivStop.setOnClickListener(view1 -> resetTime());

        return view;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Pomodoro Timer";
            String description = "Channel for Pomodoro timer notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(),
                CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer)
        // Replace with your notification icon resource
            .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)
        requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,
                builder.build());
    }

    private void resetTime() {
        if (timeCountDown != null) {
            timeCountDown.cancel();
        }
        timeProgress = 0;
        pauseOffSet = 0;
        timeCountDown = null;
        isStart = true;

        // Reset timer display to initial time (25 minutes)
        int minutes = timeSelected / 60;
        int seconds = timeSelected % 60;
        binding.tvTimeLeft.setText(String.format("%02d:%02d", minutes, seconds));
        binding.pbTimer.setProgress(0);

        // Reset visibility and button text/icons
        binding.btnStart.setVisibility(View.VISIBLE);
        binding.llstop.setVisibility(View.GONE);
        binding.ivPausePlay.setImageResource(R.drawable.play_arrow); // Set back to play icon
        setWorkingTime();
    }

    private void startTimerSetup() {
        if (timeSelected > timeProgress) {
            // Hide Start button and show pause/play and stop buttons
            binding.btnStart.setVisibility(View.GONE);
            binding.llstop.setVisibility(View.VISIBLE);

            if (isStart) {
                // Start the timer
                binding.ivPausePlay.setImageResource(R.drawable.pause); // Change icon to pause
                startTimer(pauseOffSet); // Call startTimer method here
                isStart = false;
            } else {
                // Pause the timer
                binding.ivPausePlay.setImageResource(R.drawable.play_arrow); // Change icon to play
                timePause();
                isStart = true;
            }
        } else {
            Toast.makeText(getContext(), "Enter Time", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTimer(long pauseOffSetL) {
        binding.pbTimer.setProgress(timeProgress);

        timeCountDown = new CountDownTimer((timeSelected * 1000L) - (pauseOffSetL * 1000), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeProgress++;
                pauseOffSet = timeSelected - millisUntilFinished / 1000;
                binding.pbTimer.setProgress(timeSelected - timeProgress);

                int minutes = (timeSelected - timeProgress) / 60;
                int seconds = (timeSelected - timeProgress) % 60;
                binding.tvTimeLeft.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                resetTime();
                Toast.makeText(getContext(), "Time's Up!", Toast.LENGTH_SHORT).show();
                // Show notification
                showNotification("Pomodoro Timer", "Your study session has ended!");
                setBreakTime();
            }
        }.start();
    }

    private void timePause() {
        if (timeCountDown != null) {
            timeCountDown.cancel();
        }
    }

    private void setWorkingTime() {
        // Set initial time to 25 minutes (1500 seconds)
        timeSelected = 1 * 60;
        binding.pbTimer.setMax(timeSelected);

        // Display 25 minutes on the timer
        int displayMinutes = timeSelected / 60;
        int displaySeconds = timeSelected % 60;
        binding.tvTimeLeft.setText(String.format("%02d:%02d", displayMinutes, displaySeconds));
        binding.btnStart.setText("Start");
    }

    private void setBreakTime() {
        // Set initial time to 25 minutes (1500 seconds)
        timeSelected = 1/2 * 60;
        binding.pbTimer.setMax(timeSelected);

        // Display 25 minutes on the timer
        int displayMinutes = timeSelected / 60;
        int displaySeconds = timeSelected % 60;
        binding.tvTimeLeft.setText(String.format("%02d:%02d", displayMinutes, displaySeconds));
        binding.btnStart.setText("Break");

        new CountDownTimer(timeSelected * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // ... (existing code)
            }

            @Override
            public void onFinish() {
                resetTime();
                Toast.makeText(getContext(), "Break Time's Up!", Toast.LENGTH_SHORT).show();
                setWorkingTime(); // Start a new work session

                // Show notification for the start of a new work session
                showNotification("Pomodoro Timer", "New work session started!");
            }
        }.start();
    }


}
