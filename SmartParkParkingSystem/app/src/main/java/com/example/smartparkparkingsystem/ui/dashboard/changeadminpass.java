package com.example.smartparkparkingsystem.ui.dashboard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changeadminpass extends AppCompatActivity {

    private EditText oldPassInput, newPassInput, confirmPassInput;
    private Button resetButton;
    private FirebaseAuth mAuth;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass); // reuse pass.xml

        mAuth = FirebaseAuth.getInstance();

        // Match your pass.xml IDs
        oldPassInput = findViewById(R.id.pass);
        newPassInput = findViewById(R.id.newpass);
        confirmPassInput = findViewById(R.id.newpass1);
        resetButton = findViewById(R.id.resetButton);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        resetButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPass = oldPassInput.getText().toString().trim();
        String newPass = newPassInput.getText().toString().trim();
        String confirmPass = confirmPassInput.getText().toString().trim();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.equals(oldPass)) {
            Toast.makeText(this, "New password cannot be the same as old password", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser admin = mAuth.getCurrentUser();
        if (admin == null || admin.getEmail() == null) {
            Toast.makeText(this, "No admin logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(admin.getEmail(), oldPass);
        admin.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                admin.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(changeadminpass.this,
                                "Admin password updated successfully", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(changeadminpass.this,
                                "Failed to update admin password", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(changeadminpass.this,
                        "Old password is incorrect", Toast.LENGTH_LONG).show();
            }
        });
    }
}
