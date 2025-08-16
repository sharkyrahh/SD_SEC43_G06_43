package com.example.smartparkparkingsystem;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button signInButton, forgotPassButton, signUpButton;
    private TextView msgLabel;
    private ToggleButton roleToggle;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        emailEditText = findViewById(R.id.emaileditText);
        passwordEditText = findViewById(R.id.passwordeditText);
        signInButton = findViewById(R.id.signInButton);
        msgLabel = findViewById(R.id.msgLabel);
        roleToggle = findViewById(R.id.exampleToggle);
        signUpButton = findViewById(R.id.signupButton);
        forgotPassButton = findViewById(R.id.forgotpass);

        // underline "New user? Sign Up instead"
        msgLabel.setPaintFlags(msgLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Navigate to SignUp page
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
        });

        // Forgot password navigation
        forgotPassButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPassActivity.class);
            startActivity(intent);
        });

        // Sign In button → login and check role
        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String expectedRole = roleToggle.isChecked() ? "Admin" : "User";

            if (!validateInputs(email, password)) return;

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                dbRef.child(expectedRole + "s").child(user.getUid()).get()
                                        .addOnCompleteListener(dataTask -> {
                                            if (dataTask.isSuccessful() && dataTask.getResult().exists()) {
                                                // ✅ role matches → allow login
                                                Toast.makeText(MainActivity.this,
                                                        expectedRole + " login successful",
                                                        Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // ❌ role mismatch
                                                mAuth.signOut();
                                                Toast.makeText(MainActivity.this,
                                                        "You are not registered as " + expectedRole,
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Login failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
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
