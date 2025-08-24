package com.example.smartparkparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button signInButton, forgotPassButton, signInUserButton;

    private FirebaseAuth mAuth;
    private DatabaseReference roleRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();

        // Database reference to Role node
        roleRef = FirebaseDatabase.getInstance(
                "https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("Role");

        // UI elements
        emailEditText = findViewById(R.id.emaileditText);
        passwordEditText = findViewById(R.id.passwordeditText);
        signInButton = findViewById(R.id.signInButton);
        forgotPassButton = findViewById(R.id.forgotpass);
        signInUserButton = findViewById(R.id.signInUserButton); // optional navigation

        // Forgot password navigation
        forgotPassButton.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ForgotPassActivity.class));
        });

        // ===================== ADMIN LOGIN =====================
        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!validateInputs(email, password)) return;

            // Login with Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Check admin role in Realtime Database
                                roleRef.orderByChild("email").equalTo(email)
                                        .get().addOnCompleteListener(roleTask -> {
                                            if (roleTask.isSuccessful() && roleTask.getResult().exists()) {
                                                for (DataSnapshot ds : roleTask.getResult().getChildren()) {
                                                    String role = ds.child("role").getValue(String.class);
                                                    if ("admin".equals(role)) {
                                                        Toast.makeText(AdminActivity.this,
                                                                "Login successful as Admin",
                                                                Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(AdminActivity.this, DashboardActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(AdminActivity.this,
                                                                "Not authorized as Admin",
                                                                Toast.LENGTH_LONG).show();
                                                        mAuth.signOut();
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(AdminActivity.this,
                                                        "Admin email not found",
                                                        Toast.LENGTH_LONG).show();
                                                mAuth.signOut();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(AdminActivity.this,
                                    "Login failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
        // ======================================================

        // Optional: Navigate to User login area
        signInUserButton.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, MainActivity.class));
        });
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return false;
        }
        return true;
    }
}
