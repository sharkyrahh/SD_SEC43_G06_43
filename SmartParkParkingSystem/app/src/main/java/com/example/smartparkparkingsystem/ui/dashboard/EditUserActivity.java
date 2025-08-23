package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.R;

public class EditUserActivity extends AppCompatActivity {

    String currentUID;
    Button registerRFID;

    TextView cardUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        registerRFID = findViewById(R.id.btnRegisterRFID);

        registerRFID.setOnClickListener(v-> {
                Intent intent = new Intent(EditUserActivity.this, ScanActivity.class);
                startActivity(intent);
        });

        cardUID = findViewById(R.id.cardUID);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("CARD_UID")) {
            currentUID = intent.getStringExtra("CARD_UID");
            String string = "Card UID: " + currentUID;
            cardUID.setText(string); }
    }
}