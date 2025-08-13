// src/main/java/com/example/smartparkparkingsystem/ForgotPasswordActivity.java
package com.example.smartparkparkingsystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPassActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        emailInput = findViewById(R.id.emailInput);
        resetButton = findViewById(R.id.resetButton);

        resetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(ForgotPassActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                // Backend integration goes here
                // Example: send password reset link via API
                Toast.makeText(ForgotPassActivity.this, "Password reset link sent to " + email, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

