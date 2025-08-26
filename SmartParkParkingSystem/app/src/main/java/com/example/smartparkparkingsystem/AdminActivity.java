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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button signInButton, forgotPassButton;
    private TextView signInUserText;

    private FirebaseAuth auth;
    private DatabaseReference roleRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        auth = FirebaseAuth.getInstance();

        // Reference ke node Role/ID
        roleRef = FirebaseDatabase
                .getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Role")
                .child("ID");

        // UI elements
        emailEditText = findViewById(R.id.emaileditText);
        passwordEditText = findViewById(R.id.passwordeditText);
        signInButton = findViewById(R.id.signInButton);
        forgotPassButton = findViewById(R.id.forgotPass);
        signInUserText = findViewById(R.id.signinusertext);

        signInUserText.setPaintFlags(signInUserText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Forgot password navigation
        forgotPassButton.setOnClickListener(v ->
                startActivity(new Intent(AdminActivity.this, ForgotPassActivity.class)));

        // ===================== ADMIN LOGIN =====================
        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!validateInputs(email, password)) return;

            // Sign in with FirebaseAuth
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                if (user.isEmailVerified()) {
                                    // Now check Role in database
                                    roleRef.get().addOnCompleteListener(roleTask -> {
                                        if (roleTask.isSuccessful() && roleTask.getResult().exists()) {
                                            DataSnapshot snapshot = roleTask.getResult();
                                            String dbEmail = snapshot.child("email").getValue(String.class);
                                            String dbRole = snapshot.child("role").getValue(String.class);

                                            if (dbEmail != null && dbRole != null &&
                                                    dbEmail.equalsIgnoreCase(email) &&
                                                    dbRole.equalsIgnoreCase("admin")) {

                                                Toast.makeText(AdminActivity.this,
                                                        "Login successful", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(AdminActivity.this, DashboardActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(AdminActivity.this,
                                                        "Not authorized as admin",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(AdminActivity.this,
                                                    "Admin record not found",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    // Not verified yet → send verification email
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(sendTask -> {
                                                if (sendTask.isSuccessful()) {
                                                    Toast.makeText(AdminActivity.this,
                                                            "Please verify your email. Verification link sent to: " + user.getEmail(),
                                                            Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(AdminActivity.this,
                                                            "Failed to send verification: " +
                                                                    sendTask.getException().getMessage(),
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            }
                        } else {
                            Toast.makeText(AdminActivity.this,
                                    "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
        // ======================================================

        // Button to go back to normal user login
        signInUserText.setOnClickListener(v ->
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
