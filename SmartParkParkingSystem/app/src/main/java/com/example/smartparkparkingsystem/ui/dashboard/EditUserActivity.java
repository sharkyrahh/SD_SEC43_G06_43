package com.example.smartparkparkingsystem.ui.dashboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditUserActivity extends AppCompatActivity {

    private EditText editFullName, editEmail, cardUID, editPlateNumber;
    private Button registerRFID, btnSaveProfile;
    private ImageView backButton;

    private String userId;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        // Bind views
        backButton = findViewById(R.id.backButton);
        registerRFID = findViewById(R.id.btnRegisterRFID);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        editFullName = findViewById(R.id.editFullName);
        editEmail = findViewById(R.id.editEmail);
        cardUID = findViewById(R.id.cardUID);
        editPlateNumber = findViewById(R.id.editPlateNumber);

        // Get userId from intent
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        backButton.setOnClickListener(v -> finish());

        // Load existing user data
        loadUserData();

        // Save changes
        btnSaveProfile.setOnClickListener(v -> saveChanges());

        // Register RFID placeholder
        registerRFID.setOnClickListener(v ->
                Toast.makeText(this, "Redirect to RFID scan not implemented yet", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    editFullName.setText(snapshot.child("fullName").getValue(String.class));
                    editEmail.setText(snapshot.child("email").getValue(String.class));
                    cardUID.setText(snapshot.child("cardUID").getValue(String.class));
                    editPlateNumber.setText(snapshot.child("plateNumber").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditUserActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveChanges() {
        String fullName = editFullName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String uid = cardUID.getText().toString().trim();
        String plateNumber = editPlateNumber.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Full name and email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", fullName);
        updates.put("email", email);
        updates.put("cardUID", uid);
        updates.put("plateNumber", plateNumber);

        // Save instantly to Firebase
        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditUserActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    // Go back to UserListActivity after saving
                    Intent intent = new Intent(EditUserActivity.this, UserListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // close EditUserActivity
                })
                .addOnFailureListener(e ->
                        Toast.makeText(EditUserActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
