package com.antopina.schedulingappointmentplanner.LoginSignup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.antopina.schedulingappointmentplanner.HomePage.HomePageView;
import com.antopina.schedulingappointmentplanner.LoginSignup.ResetPassword.ForgotPassword;
import com.antopina.schedulingappointmentplanner.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomePageView.class));
            finish();
        }

        binding.btLogin.setOnClickListener(view -> {
            try {
                String email = binding.etEmail.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();

                // set Error for fields
                if(TextUtils.isEmpty(email)){
                    binding.etEmail.setError("Required Field");
                    binding.etEmail.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    binding.etPassword.setError("Required Field");
                    binding.etPassword.requestFocus();
                    return;
                }

                binding.progressBar2.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomePageView.class));
                        } else {
                            Toast.makeText(Login.this, "Error: 11" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//            Intent intent = new Intent(getApplicationContext(), EmailVerification.class);
//            startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvForgot.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ResetPasswordView.class);
            startActivity(intent);
        });

        binding.tvSignup.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Signup.class);
            startActivity(intent);
        });

    }

}