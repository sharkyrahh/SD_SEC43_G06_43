package com.example.smartparkparkingsystem;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button signInButton, forgotPassButton, signInUserButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();

        // UI elements
        emailEditText = findViewById(R.id.emaileditText);
        passwordEditText = findViewById(R.id.passwordeditText);
        signInButton = findViewById(R.id.signInButton);
        forgotPassButton = findViewById(R.id.forgotpass);
        signInUserButton = findViewById(R.id.signInUserButton); // bind admin button

        // Forgot password navigation
        forgotPassButton.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ForgotPassActivity.class));
        });

        // ===================== USER LOGIN =====================
        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!validateInputs(email, password)) return;

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Toast.makeText(AdminActivity.this,
                                        "Login successful",
                                        Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(AdminActivity.this, DashboardActivity.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(AdminActivity.this,
                                    "Login failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
        // ======================================================

        // ===================== ADMIN LOGIN AREA =====================
        // This section is ONLY for Admin Login.
        // For now → It simply navigates to AdminActivity (which will load modify_xml.xml layout).
        // Later → You should add Firestore check to verify that the user has "role = admin".
        //
        // Example Firestore schema (users collection):
        // users -> userId -> { email: "admin@gmail.com", fullName: "System Admin", role: "admin" }
        //
        // Then, before navigating, query Firestore:
        // if role == "admin" → allow access
        // else → block with Toast ("Not authorized as Admin").
        signInUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
            startActivity(intent);
        });
        // ============================================================
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
