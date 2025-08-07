package com.example.smartparkparkingsystem.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartparkparkingsystem.HomeActivity;
import com.example.smartparkparkingsystem.LoginActivity;
import com.example.smartparkparkingsystem.R;

public class EditProfileActivity extends AppCompatActivity {

    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        // Back button pergi balik Profile Fragment. Code ni boleh stay/gunakan je
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v-> onBackPressed());


    }
}