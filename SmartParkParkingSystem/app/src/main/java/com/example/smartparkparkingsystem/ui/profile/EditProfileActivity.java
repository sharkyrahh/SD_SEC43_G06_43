package com.example.smartparkparkingsystem.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    import com.example.smartparkparkingsystem.R;


EditText editFullName, editEmail, editPassword;
    Button btnSaveProfile;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.attr.layout);

        editFullName = findViewById(android.R.attr.id);
        editEmail = findViewById(android.R.attr.id);
        editPassword = findViewById(android.R.attr.id);
        btnSaveProfile = findViewById(android.R.attr.id);

        btnSaveProfile.setOnClickListener(v -> {
            String fullName = editFullName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Save changes to database
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}




// hi
