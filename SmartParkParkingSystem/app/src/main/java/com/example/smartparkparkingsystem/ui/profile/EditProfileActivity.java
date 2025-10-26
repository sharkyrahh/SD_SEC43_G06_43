package com.example.smartparkparkingsystem.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
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

    EditText editFullName, editPlateNum;
    Button btnSaveProfile, btnChangePassword;
    ImageView backButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);


        editFullName = findViewById(R.id.editFullName);
        editPlateNum = findViewById(R.id.platenum);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        backButton = findViewById(R.id.backButton);

        editPlateNum.setFilters(new InputFilter[]{
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(10)
        });


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(
                "https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("users");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            mDatabase.child(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    editFullName.setText(task.getResult().child("fullName").getValue(String.class));
                    editPlateNum.setText(task.getResult().child("plateNumber").getValue(String.class));
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        }


        backButton.setOnClickListener(v -> finish());


        btnSaveProfile.setOnClickListener(v -> {
            String fullName = editFullName.getText().toString().trim();
            String plateNum = editPlateNum.getText().toString().trim();

            if (fullName.isEmpty() || plateNum.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (currentUser != null) {
                String uid = currentUser.getUid();
                Map<String, Object> updates = new HashMap<>();
                updates.put("fullName", fullName);
                updates.put("plateNumber", plateNum);

                mDatabase.child(uid).updateChildren(updates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(EditProfileActivity.this, ChangePassActivity.class);
            startActivity(intent);
        });
    }
}
