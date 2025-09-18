package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.R;
import com.example.smartparkparkingsystem.ui.profile.ChangePassActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Editprofile extends AppCompatActivity {

    private EditText editFullName, editPhone; // editPhone uses view id 'platenum' from XML
    private Button btnSaveProfile, btnChangePassword;
    private ImageView backButton;

    private FirebaseAuth mAuth;
    private DatabaseReference roleRef; // points to Role/ID in RTDB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admineditprofile); // must match your XML filename

        // Views (IDs must match your XML)
        editFullName      = findViewById(R.id.editFullName);
        editPhone         = findViewById(R.id.platenum);
        btnSaveProfile    = findViewById(R.id.btnSaveProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        backButton        = findViewById(R.id.backButton);

        // Firebase
        mAuth   = FirebaseAuth.getInstance(); // kept for Change Password flow
        roleRef = FirebaseDatabase.getInstance(
                "https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("Role").child("ID"); // EXACT path you use for admin record

        // Load current admin profile from Role/ID
        roleRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                String currentName  = task.getResult().child("fullname").getValue(String.class);
                String currentPhone = task.getResult().child("Phonenumber").getValue(String.class);

                editFullName.setText(currentName != null ? currentName : "");
                editPhone.setText(currentPhone != null ? currentPhone : "");
            } else {
                Toast.makeText(Editprofile.this, "Failed to load admin profile", Toast.LENGTH_SHORT).show();
            }
        });

        // Back
        backButton.setOnClickListener(v -> finish());

        // Save changes (updates ONLY fullname & Phonenumber under Role/ID)
        btnSaveProfile.setOnClickListener(v -> {
            String fullName = editFullName.getText().toString().trim();
            String phone    = editPhone.getText().toString().trim();

            if (fullName.isEmpty()) {
                Toast.makeText(this, "Please fill in your name.", Toast.LENGTH_SHORT).show();
                editFullName.requestFocus();
                return;
            }

            if (phone.isEmpty()) {
                Toast.makeText(this, "Please fill in your phone number.", Toast.LENGTH_SHORT).show();
                editPhone.requestFocus();
                return;
            }

            if (fullName.isEmpty() && phone.isEmpty()) {
                Toast.makeText(Editprofile.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("fullname", fullName);      // EXACT key in your DB
            updates.put("Phonenumber", phone);      // EXACT key in your DB (capital P)

            roleRef.updateChildren(updates).addOnCompleteListener(updTask -> {
                if (updTask.isSuccessful()) {
                    Toast.makeText(Editprofile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(Editprofile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Change Password (opens your existing screen)
        btnChangePassword.setOnClickListener(v ->
                startActivity(new Intent(Editprofile.this, changeadminpass.class))
        );
    }
}
