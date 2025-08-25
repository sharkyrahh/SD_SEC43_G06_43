package com.example.smartparkparkingsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartparkparkingsystem.MainActivity;
import com.example.smartparkparkingsystem.R;
import com.example.smartparkparkingsystem.VerifyEmailActivity;
import com.google.firebase.auth.FirebaseAuth;

public class VerifySuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifysuccess);

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(VerifySuccessActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}