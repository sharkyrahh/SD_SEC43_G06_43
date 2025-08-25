package com.example.smartparkparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity {
    // File for FORGOT PASSWORD
    //Login button. Bila dah reset/forgot password, pergi balik Login Page.
    //loginBtn = findViewById(R.id.loginBtn);
    // loginBtn.setOnClickListener(v -> onBackPressed());
    //onBackPressed basically just Back function

    private EditText emailInput;
    private Button resetButton;
    Button loginBtn; // (This stays as you wrote)

    ImageView backButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        resetButton = findViewById(R.id.resetButton);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        // Reset password button logic
        resetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(ForgotPassActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase send password reset email
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPassActivity.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();

                    // After success → go back to login page
                    Intent intent = new Intent(ForgotPassActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ForgotPassActivity.this, "No account found with this email", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
