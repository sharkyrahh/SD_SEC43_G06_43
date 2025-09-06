package com.example.smartparkparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.ui.VerifySuccessActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyemail);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        TextView verifyMessage = findViewById(R.id.verifyMessage);
        Button resendBtn = findViewById(R.id.resendBtn);

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

        resendBtn.setOnClickListener(v -> {
            if (currentUser != null && !currentUser.isEmailVerified()) {
                currentUser.sendEmailVerification()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Verification email resent!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Failed to send verification email: " +
                                        task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.reload().addOnCompleteListener(task -> {
                        if (user.isEmailVerified()) {
                            Intent intent = new Intent(VerifyEmailActivity.this, VerifySuccessActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            handler.postDelayed(this, 3000);
                        }
                    });
                }
            }
        }, 3000);
}}
