package com.example.smartparkparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
// LOG IN file
Button loginBtn, signupBtn, forgotBtn; // test buttons for structure
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);
        forgotBtn = findViewById(R.id.forgotBtn);


        //pergi homeactivity / homepage dengan navigation bar tu semua untuk user.
        // if nak buat admin punya part bagitau, kita addkan navbar page untuk admin (?)
        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        });

        // back to sign up page
        signupBtn.setOnClickListener(v -> onBackPressed());

        // forgot password button. Nanti kalau nak buat button ni jadi text untuk kasi cantik,
        // buat je, tukar forgotBtn tu, but startActivity tu boleh still guna
        forgotBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPassActivity.class));
        });
    }
}