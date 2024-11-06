package com.antopina.schedulingappointmentplanner.LoginSignup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.databinding.ActivityEmailVerificationBinding;

public class EmailVerification extends AppCompatActivity {

    ActivityEmailVerificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityEmailVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpOtpAutoMove(binding.otp1, binding.otp2);
        setUpOtpAutoMove(binding.otp2, binding.otp3);
        setUpOtpAutoMove(binding.otp3, binding.otp4);
        setUpOtpAutoMove(binding.otp4, binding.otp5);
        setUpOtpAutoMove(binding.otp5, binding.otp6);

        binding.btSendCode.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignupSeuccess.class);
            startActivity(intent);
        });

        binding.tvLogin.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        });

    }

    private void setUpOtpAutoMove(final EditText current, final EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Move to the next EditText when the current one is filled
                if (s.length() == 1) {
                    next.requestFocus();
                }
            }
        });
    }
}