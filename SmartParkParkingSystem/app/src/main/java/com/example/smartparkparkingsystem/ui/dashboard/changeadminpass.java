package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changeadminpass extends AppCompatActivity {

    private EditText emailInput, oldPasswordInput;
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
        oldPasswordInput = findViewById(R.id.pass); // old password field you added
        resetButton = findViewById(R.id.resetButton);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        // Reset password button logic
        resetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String oldPassword = oldPasswordInput.getText().toString().trim();

            if (email.isEmpty() || oldPassword.isEmpty()) {
                Toast.makeText(changeadminpass.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(changeadminpass.this, "No admin logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            // Re-authenticate with old password
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
            currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Old password correct → send reset link
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(resetTask -> {
                        if (resetTask.isSuccessful()) {
                            Toast.makeText(changeadminpass.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                            finish(); // go back after sending reset
                        } else {
                            Toast.makeText(changeadminpass.this, "Failed to send reset link", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // Old password/email mismatch
                    Toast.makeText(changeadminpass.this, "Old password incorrect or email mismatch", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
