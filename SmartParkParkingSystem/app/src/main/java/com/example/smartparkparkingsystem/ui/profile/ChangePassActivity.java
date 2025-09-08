package com.example.smartparkparkingsystem.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassActivity extends AppCompatActivity {

    private EditText emailInput, oldPassInput;
    private Button resetButton;
    private FirebaseAuth mAuth;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        // Firebase auth
        mAuth = FirebaseAuth.getInstance();

        // Views
        emailInput = findViewById(R.id.emailInput);
        oldPassInput = findViewById(R.id.pass); // your new old password field
        resetButton = findViewById(R.id.resetButton);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        // Reset password button
        resetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String oldPass = oldPassInput.getText().toString().trim();

            if (email.isEmpty() || oldPass.isEmpty()) {
                Toast.makeText(this, "Please enter email and old password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Try reauthentication
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPass);

            // We don't need currently signed-in user for this, we create a temp one
            mAuth.signInWithEmailAndPassword(email, oldPass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        // Send reset link
                        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(resetTask -> {
                            if (resetTask.isSuccessful()) {
                                Toast.makeText(ChangePassActivity.this,
                                        "Reset link sent to your email", Toast.LENGTH_LONG).show();

                                // Redirect back
                                Intent intent = new Intent(ChangePassActivity.this, EditProfileActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(ChangePassActivity.this,
                                        "Failed to send reset link", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    // Handle error cases
                    Exception e = task.getException();
                    if (e instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(ChangePassActivity.this,
                                "Password found but email is wrong", Toast.LENGTH_LONG).show();
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(ChangePassActivity.this,
                                "Correct email but wrong password", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ChangePassActivity.this,
                                "Error: " + (e != null ? e.getMessage() : "Unknown"), Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
    }
}
