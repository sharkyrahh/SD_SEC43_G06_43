package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.R;
import com.example.smartparkparkingsystem.ui.profile.ChangePassActivity;
import com.example.smartparkparkingsystem.ui.profile.EditProfileActivity;
import com.google.firebase.auth.FirebaseAuth;

public class changeadminpass extends AppCompatActivity {

    private EditText emailInput;
    private Button resetButton;
    private FirebaseAuth mAuth;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

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
                Toast.makeText(changeadminpass.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send password reset email
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(changeadminpass.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();

                    // After success → go back to main page
                    Intent intent = new Intent(changeadminpass.this, EditProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(changeadminpass.this, "No account found with this email", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
