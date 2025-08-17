package com.example.smartparkparkingsystem;

import android.graphics.Paint;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    EditText fullNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    Button signUpButton;
    CheckBox rememberMeCheckBox;
    TextView loginLabel;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Initialize Firebase Auth & Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loginLabel = findViewById(R.id.loginLabel);
        loginLabel.setPaintFlags(loginLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);

        // Navigate to login page
        loginLabel.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, MainActivity.class);
            startActivity(intent);
        });

        // Sign up logic
        signUpButton.setOnClickListener(v -> {
            String name = fullNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.contains("@")) {
                Toast.makeText(this, "Email is invalid", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase Auth SignUp
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();

                                // Save user under "users"
                                User newUser = new User(name, email);
                                mDatabase.child("users").child(userId).setValue(newUser)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                // 🔑 Send verification email
                                                firebaseUser.sendEmailVerification()
                                                        .addOnCompleteListener(verifyTask -> {
                                                            if (verifyTask.isSuccessful()) {
                                                                Toast.makeText(SignUp.this,
                                                                        "Sign up successful! Please verify your email.",
                                                                        Toast.LENGTH_LONG).show();

                                                                // Move to VerifyEmailActivity
                                                                Intent intent = new Intent(SignUp.this, VerifyEmailActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(SignUp.this,
                                                                        "Failed to send verification email: "
                                                                                + verifyTask.getException().getMessage(),
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(SignUp.this,
                                                        "Failed to save user data",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SignUp.this,
                                    "Sign up failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    // Model class
    public static class User {
        public String fullName;
        public String email;

        public User() {
        }

        public User(String fullName, String email) {
            this.fullName = fullName;
            this.email = email;
        }
    }
}
