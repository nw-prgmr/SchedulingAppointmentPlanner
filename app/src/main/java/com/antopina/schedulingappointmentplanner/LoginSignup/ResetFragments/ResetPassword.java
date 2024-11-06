package com.antopina.schedulingappointmentplanner.LoginSignup.ResetFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.antopina.schedulingappointmentplanner.LoginSignup.ResetPasswordView;
import com.antopina.schedulingappointmentplanner.R;
import com.antopina.schedulingappointmentplanner.databinding.FragmentResetPasswordBinding;


public class ResetPassword extends Fragment {

    FragmentResetPasswordBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btResetPassword.setOnClickListener(v -> openNewFragment());


        return view;
    }

    private void openNewFragment() {
        ((ResetPasswordView) requireActivity()).replaceFragment(new ResetSuccess());
    }
}