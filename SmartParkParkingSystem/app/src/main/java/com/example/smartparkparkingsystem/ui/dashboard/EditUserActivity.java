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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
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
                    String originalEmail = snapshot.child("email").getValue(String.class);
                    editFullName.setText(snapshot.child("fullName").getValue(String.class));
                    editEmail.setText(originalEmail);
                    editEmail.setTag(originalEmail);
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

        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Please fill in your name.", Toast.LENGTH_SHORT).show();
            editFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
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

        if (TextUtils.isEmpty(plateNumber)) {
            Toast.makeText(this, "Please fill in your plate number.", Toast.LENGTH_SHORT).show();
            editPlateNumber.requestFocus();
            return;
        }

        String originalEmail = (String) editEmail.getTag();
        if (originalEmail != null && !email.equals(originalEmail)) {
            // Email was changed, check for duplicates
            checkEmailExists(fullName, email, uid, plateNumber);
        } else {
            // Email not changed, proceed with save
            proceedWithSave(fullName, email, uid, plateNumber);
        }
    }

        private void checkEmailExists(String fullName, String email, String uid, String plateNumber) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean emailExists = false;

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        // Check if the email belongs to a different user (not the current one being edited)
                        if (!userSnapshot.getKey().equals(userId)) {
                            emailExists = true;
                            break;
                        }
                    }

                    if (emailExists) {
                        Toast.makeText(EditUserActivity.this, "Email is already registered by another user", Toast.LENGTH_SHORT).show();
                        editEmail.setError("Email already exists");
                        editEmail.requestFocus();
                    } else {
                        proceedWithSave(fullName, email, uid, plateNumber);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditUserActivity.this, "Error checking email: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void proceedWithSave(String fullName, String email, String uid, String plateNumber) {
            boolean hasEnter = false;

            if (newCardRegister) {
                Card newCard = new Card(plateNumber, userId, hasEnter);

                DatabaseReference cardRef = FirebaseDatabase.getInstance().getReference().child("cards").child(uid);
                cardRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(EditUserActivity.this, "Card already exists. Please try again!", Toast.LENGTH_SHORT).show();
                            newCardRegister = false;
                            cardUID.setText("");
                            return;
                        } else {
                            mDatabase.child("cards").child(uid).setValue(newCard)
                                    .addOnSuccessListener(aVoid -> {
                                        updateProfile(fullName, email, uid, plateNumber);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(EditUserActivity.this, "Failed to save card: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(EditUserActivity.this, "Error checking card: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                updateProfile(fullName, email, uid, plateNumber);
            }
        }

    private void updateProfile(String fullName, String email, String uid, String plateNumber) {
        // First update email in Firebase Authentication if it was changed
        String originalEmail = (String) editEmail.getTag();
        if (originalEmail != null && !email.equals(originalEmail)) {
            updateAuthEmail(email, fullName, uid, plateNumber);
        } else {
            // Email not changed, just update database
            updateDatabaseProfile(fullName, email, uid, plateNumber);
        }
    }

    private void updateAuthEmail(String newEmail, String fullName, String dbUid, String plateNumber) {
        // Get current user from Firebase Authentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.updateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Email updated in Authentication, now update database
                            updateDatabaseProfile(fullName, newEmail, dbUid, plateNumber);
                        } else {
                            // Handle errors - often requires recent login
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(EditUserActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                            } else if (exception instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(EditUserActivity.this, "Email is already in use", Toast.LENGTH_SHORT).show();
                            } else if (exception instanceof FirebaseAuthRecentLoginRequiredException) {
                                // User needs to re-authenticate
                                Toast.makeText(EditUserActivity.this, "Please re-login to change email", Toast.LENGTH_SHORT).show();
                                // You might want to redirect to login page here
                            } else {
                                Toast.makeText(EditUserActivity.this, "Failed to update email: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDatabaseProfile(String fullName, String email, String uid, String plateNumber) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", fullName);
        updates.put("email", email);
        updates.put("cardUID", uid);
        updates.put("plateNumber", plateNumber);

        // Save to Firebase Database
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

public class Card {
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

