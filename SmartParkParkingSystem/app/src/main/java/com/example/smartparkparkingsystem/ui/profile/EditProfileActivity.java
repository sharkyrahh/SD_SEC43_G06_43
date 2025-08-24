package com.example.smartparkparkingsystem.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    EditText editFullName, editEmail;
    Button btnSaveProfile, btnChangePassword;
    ImageView backButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        // Initialize views
        editFullName = findViewById(R.id.editFullName);
        editEmail = findViewById(R.id.editEmail);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        backButton = findViewById(R.id.backButton);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(
                "https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("users");

        // Load current data from database for this user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            mDatabase.child(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String currentName = task.getResult().child("fullName").getValue(String.class);
                    String currentEmail = task.getResult().child("email").getValue(String.class);
                    editFullName.setText(currentName != null ? currentName : "");
                    editEmail.setText(currentEmail != null ? currentEmail : "");
                }
            });
        }

        // Back button click
        backButton.setOnClickListener(v -> finish());

        // Save changes click
        btnSaveProfile.setOnClickListener(v -> {
            String fullName = editFullName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (currentUser != null) {
                    String uid = currentUser.getUid();
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("fullName", fullName);
                    updates.put("email", email);

                    mDatabase.child(uid).updateChildren(updates).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Change Password button click
        if (btnChangePassword != null) {
            btnChangePassword.setOnClickListener(v -> {
                Intent intent = new Intent(EditProfileActivity.this, ChangePassActivity.class);
                startActivity(intent);
            });
        }
    }
}
