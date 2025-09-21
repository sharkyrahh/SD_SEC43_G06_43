package com.example.smartparkparkingsystem.ui.dashboard;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    String currentUID;
    private DatabaseReference userRef;
    private DatabaseReference mDatabase;
    private DatabaseReference rfidRef;
    private ActivityResultLauncher<Intent> scanLauncher;

    private boolean newCardRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        scanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.hasExtra("CARD_UID")) {
                            currentUID = data.getStringExtra("CARD_UID");
                            cardUID.setText(currentUID);
                            newCardRegister = true;
                        }
                    }
                }
        );

        // Bind views
        backButton = findViewById(R.id.backButton);
        registerRFID = findViewById(R.id.btnRegisterRFID);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        editFullName = findViewById(R.id.editFullName);
        editEmail = findViewById(R.id.editEmail);
        cardUID = findViewById(R.id.cardUID);
        editPlateNumber = findViewById(R.id.editPlateNumber);

        editPlateNumber.setFilters(new InputFilter[]{
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(10)
        });

        // Get userId from intent
        Intent intentUser = getIntent();
        userId = intentUser.getStringExtra("userId");
        if (!intentUser.hasExtra("userId")) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        mDatabase = FirebaseDatabase
                .getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app");
        rfidRef = database.getReference("RFID");
        rfidRef.child("registerMode").setValue(false);

        backButton.setOnClickListener(v -> finish());

        // Load existing user data
        loadUserData();

        // Save changes
        btnSaveProfile.setOnClickListener(v -> saveChanges());

        Intent intentCard = new Intent(EditUserActivity.this, ScanActivity.class);
        registerRFID.setOnClickListener(v ->
                scanLauncher.launch(intentCard)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        rfidRef.child("registerMode").setValue(false);
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

        if (TextUtils.isEmpty(fullName)){
            Toast.makeText(this, "Please fill in your name.", Toast.LENGTH_SHORT).show();
            editFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please fill in your email.", Toast.LENGTH_SHORT).show();
            editEmail.requestFocus();
            return;
        }

        if (!email.contains("@")) {
            editEmail.setError("Missing '@'");
            editEmail.requestFocus();
            return;
        }

        if (!email.contains(".com") && !email.contains(".my")) {
            editEmail.setError("Missing '.com'");
            editEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(plateNumber)){
            Toast.makeText(this, "Please fill in your name.", Toast.LENGTH_SHORT).show();
            editPlateNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(fullName) && TextUtils.isEmpty(email) && TextUtils.isEmpty(plateNumber)) {
            Toast.makeText(this, "Full name and email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasEnter = false;

        if (newCardRegister) {
            Card newCard = new Card(plateNumber, userId, hasEnter);

            DatabaseReference cardRef = FirebaseDatabase.getInstance().getReference().child("cards").child(uid);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(EditUserActivity.this, "Card already exists. Please try again!", Toast.LENGTH_SHORT).show();
                        newCardRegister = false;
                        cardUID.setText("");
                        return;
                    } else {
                        mDatabase.child("cards").child(uid).setValue(newCard);
                        updateProfile(fullName, email, uid, plateNumber);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(EditUserActivity.this, "Error checking card: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            cardRef.addListenerForSingleValueEvent(valueEventListener);
        }
        else {
            updateProfile(fullName, email, uid, plateNumber);
        }
    }

    public void updateProfile(String fullName, String email, String uid, String plateNumber){
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

    public static class Card {
        public String plateNum;
        public String UID;
        public boolean hasEntered;
        public Card() {
        }

        public Card(String plateNum, String UID, boolean hasEntered) {
            this.plateNum = plateNum;
            this.UID = UID;
            this.hasEntered = hasEntered;
        }
    }
}
