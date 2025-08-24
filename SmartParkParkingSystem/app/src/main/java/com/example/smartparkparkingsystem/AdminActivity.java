package com.example.smartparkparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button signInButton, forgotPassButton, signInUserButton;

    private DatabaseReference roleRef; // reference to Role/ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Reference ke node Role/ID
        roleRef = FirebaseDatabase
                .getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Role")
                .child("ID");

        // UI elements
        emailEditText = findViewById(R.id.emaileditText);
        passwordEditText = findViewById(R.id.passwordeditText);
        signInButton = findViewById(R.id.signInButton);
        forgotPassButton = findViewById(R.id.forgotpass);
        signInUserButton = findViewById(R.id.signInUserButton);

        // Forgot password navigation
        forgotPassButton.setOnClickListener(v ->
                startActivity(new Intent(AdminActivity.this, ForgotPassActivity.class)));

        // ===================== ADMIN LOGIN =====================
        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!validateInputs(email, password)) return;

            // Direct ambil data dari Role/ID
            roleRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    DataSnapshot snapshot = task.getResult();

                    String dbEmail = snapshot.child("email").getValue(String.class);
                    String dbPassword = snapshot.child("password").getValue(String.class);
                    String dbRole = snapshot.child("role").getValue(String.class);

                    if (dbEmail != null && dbPassword != null && dbRole != null) {
                        if (dbEmail.equalsIgnoreCase(email)
                                && dbPassword.equals(password)
                                && dbRole.equalsIgnoreCase("admin")) {

                            Toast.makeText(AdminActivity.this,
                                    "Login successful", Toast.LENGTH_SHORT).show();

                            // go to Dashboard
                            startActivity(new Intent(AdminActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            Toast.makeText(AdminActivity.this,
                                    "Invalid credentials or not admin",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AdminActivity.this,
                                "Admin data incomplete in database",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AdminActivity.this,
                            "No admin record found",
                            Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e ->
                    Toast.makeText(AdminActivity.this,
                            "Database error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show()
            );
        });
        // ======================================================

        // Button to go back to normal user login
        signInUserButton.setOnClickListener(v ->
                startActivity(new Intent(AdminActivity.this, MainActivity.class)));
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
