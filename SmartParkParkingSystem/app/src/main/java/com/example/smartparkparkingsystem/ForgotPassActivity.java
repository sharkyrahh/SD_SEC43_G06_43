package com.example.smartparkparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgotPassActivity extends AppCompatActivity {
// File for FORGOT PASSWORD

    Button loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        //Login button. Bila dah reset/forgot password, pergi balik Login Page.
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> onBackPressed());
        //onBackPressed basically just Back function
    }
}