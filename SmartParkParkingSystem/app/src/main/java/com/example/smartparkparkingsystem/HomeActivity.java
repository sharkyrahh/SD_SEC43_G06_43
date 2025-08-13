package com.example.smartparkparkingsystem;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeText, infoText;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // pastikan XML anda betul

        welcomeText = findViewById(R.id.welcomeText);
        infoText = findViewById(R.id.infoText);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String userId = mAuth.getCurrentUser().getUid();

        // Ambil nama pengguna dari database
        mDatabase.child("users").child(userId).child("fullName").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String fullName = task.getResult().getValue(String.class);
                welcomeText.setText("Welcome, " + fullName + "!");
                infoText.setText("This is your SmartPark dashboard. Tap the menu to get started.");
            } else {
                welcomeText.setText("Welcome!");
                infoText.setText("Failed to load user data.");
            }
        });


    }
}