package com.antopina.schedulingappointmentplanner.LoginSignup.ResetFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.antopina.schedulingappointmentplanner.LoginSignup.ResetPasswordView;
import com.antopina.schedulingappointmentplanner.R;

public class ForgotPassword extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_forgot_password, container, false);

        Button btSendCode = view.findViewById(R.id.btSendCode);
        btSendCode.setOnClickListener(v -> openNewFragment());

        return view;
    }

    private void openNewFragment() {
        ((ResetPasswordView) requireActivity()).replaceFragment(new GetCode());
    }
}