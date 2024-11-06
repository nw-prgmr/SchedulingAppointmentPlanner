package com.antopina.schedulingappointmentplanner.LoginSignup.ResetFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.antopina.schedulingappointmentplanner.LoginSignup.ResetPasswordView;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.databinding.FragmentGetCodeBinding;

public class GetCode extends Fragment {

    private FragmentGetCodeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGetCodeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Button to navigate to ResetPassword fragment
        binding.btSubmit.setOnClickListener(v -> openNewFragment());

        // Set up OTP auto-move
        setUpOtpAutoMove(binding.otp1, binding.otp2);
        setUpOtpAutoMove(binding.otp2, binding.otp3);
        setUpOtpAutoMove(binding.otp3, binding.otp4);
        setUpOtpAutoMove(binding.otp4, binding.otp5);
        setUpOtpAutoMove(binding.otp5, binding.otp6);

        return view;
    }

    private void openNewFragment() {
        ((ResetPasswordView) requireActivity()).replaceFragment(new ResetPassword());
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

    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}