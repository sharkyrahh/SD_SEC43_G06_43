package com.example.smartparkparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditUserActivity extends AppCompatActivity {

    Button registerRFID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        registerRFID = findViewById(R.id.btnRegisterRFID);

        registerRFID.setOnClickListener(v-> {
                Intent intent = new Intent(EditUserActivity.this, ScanActivity.class);
                startActivity(intent);
        });
    }
}