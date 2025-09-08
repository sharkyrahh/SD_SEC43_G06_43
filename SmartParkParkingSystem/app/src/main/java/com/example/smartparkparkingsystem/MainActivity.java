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

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button signInButton, forgotPassButton, signUpButton;
    private TextView msgLabel, signInAdminText; // changed to TextView

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();

        // UI elements
        emailEditText = findViewById(R.id.emaileditText);
        passwordEditText = findViewById(R.id.passwordeditText);
        signInButton = findViewById(R.id.signInButton);
        msgLabel = findViewById(R.id.msgLabel);
        signUpButton = findViewById(R.id.signupButton);
        forgotPassButton = findViewById(R.id.forgotpass);
        signInAdminText = findViewById(R.id.signInAdminText); // now TextView

        signInAdminText.setPaintFlags(signInAdminText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        // Navigate to SignUp page
        signUpButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUp.class));
        });

        // Forgot password navigation
        forgotPassButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ForgotPassActivity.class));
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
                                Toast.makeText(MainActivity.this,
                                        "Login successful",
                                        Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Login failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
        // ======================================================

        // ===================== ADMIN LOGIN AREA =====================
        signInAdminText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
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

        if (!email.contains("@")) {
            emailEditText.setError("Missing '@'");
            emailEditText.requestFocus();
            return false;
        }

        if (!email.contains(".com") && !email.contains(".my")) {
            emailEditText.setError("Missing '.com'");
            emailEditText.requestFocus();
            return false;
        }

        int atCount = email.length() - email.replace("@", "").length();

        if (atCount > 1) {
            emailEditText.setError("Too many '@'s");
            emailEditText.requestFocus();
            return false;
        }

        if (email.lastIndexOf(".") < email.indexOf("@")) {
            emailEditText.setError("Invalid email format");
            emailEditText.requestFocus();
            return false;
        }

        if (email.indexOf(".") - email.indexOf("@") <= 1) {
            emailEditText.setError("Invalid email format");
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

