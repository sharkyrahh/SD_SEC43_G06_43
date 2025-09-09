package com.example.smartparkparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
                emailInput.setError("Please enter your email.");
                emailInput.requestFocus();
                return;
            }

            if (!email.contains("@")) {
                emailInput.setError("Missing '@'");
                emailInput.requestFocus();
                return;
            }

            if (!email.contains(".com") && !email.contains(".my")) {
                emailInput.setError("Missing '.com'");
                emailInput.requestFocus();
                return;
            }

            int atCount = email.length() - email.replace("@", "").length();

            if (atCount > 1) {
                emailInput.setError("Too many '@'s");
                emailInput.requestFocus();
                return;
            }

            if (email.lastIndexOf(".") < email.indexOf("@")) {
                emailInput.setError("Invalid email format");
                emailInput.requestFocus();
                return;
            }

            if (email.indexOf(".") - email.indexOf("@") <= 1) {
                emailInput.setError("Invalid email format");
                emailInput.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Please enter a valid email");
                emailInput.requestFocus();
                return;
            }

            // Firebase send password reset email
            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().getSignInMethods() != null &&
                            !task.getResult().getSignInMethods().isEmpty()) {

                        // Email is registered - send reset email
                        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(sendTask -> {
                            if (sendTask.isSuccessful()) {
                                Toast.makeText(ForgotPassActivity.this,
                                        "Reset link sent to your email",
                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ForgotPassActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(ForgotPassActivity.this,
                                        "Failed to send reset email",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        // Email is NOT registered
                        Toast.makeText(ForgotPassActivity.this,
                                "No account found with this email",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ForgotPassActivity.this,
                            "Error checking email",
                            Toast.LENGTH_LONG).show();
                }
            });
        });
}}