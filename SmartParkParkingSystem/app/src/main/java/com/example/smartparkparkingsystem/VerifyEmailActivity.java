package com.example.smartparkparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyemail); // <-- your XML file

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        TextView verifyMessage = findViewById(R.id.verifyMessage);
        Button loginBtn = findViewById(R.id.loginBtn);

        // Send verification email if user exists
        if (currentUser != null && !currentUser.isEmailVerified()) {
            currentUser.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to send verification email: " +
                                    task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

        // When user clicks button -> go back to login
        loginBtn.setOnClickListener(v -> {
            // Sign out first so user must log in again
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(VerifyEmailActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
