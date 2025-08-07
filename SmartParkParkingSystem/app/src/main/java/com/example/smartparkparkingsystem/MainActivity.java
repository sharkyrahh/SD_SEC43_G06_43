package com.example.smartparkparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
// Starting file. Use this file for SIGN UP

    Button verifyBtn, signinBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyBtn = findViewById(R.id.verifyBtn);
        signinBtn = findViewById(R.id.signinBtn);

        // test button to proceed sign up. Nanti tukar nama la biar tak confuse.
        // since once sign up successful, kena verify email dulu kan thats why pergi page verify email
        verifyBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, VerifyEmailActivity.class));
        });

        // switch to sign up page
        signinBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }
}