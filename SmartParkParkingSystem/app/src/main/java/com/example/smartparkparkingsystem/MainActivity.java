package com.example.smartparkparkingsystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText fullNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    Button signUpButton, googleBtn, appleBtn, facebookBtn;
    CheckBox rememberMeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        googleBtn = findViewById(R.id.googleBtn);
        appleBtn = findViewById(R.id.appleBtn); // <- changed here
        facebookBtn = findViewById(R.id.facebookBtn);

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
                Toast.makeText(this, "Passwords need to be more than eight letter", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!email.contains("@")) {
                Toast.makeText(this, "Email is invalid", Toast.LENGTH_SHORT).show();
                return;
            }


            boolean remember = rememberMeCheckBox.isChecked();
            Toast.makeText(this, "Signed up " + (remember ? "with Remember Me" : ""), Toast.LENGTH_SHORT).show();

            // Proceed to verification or dashboard
        });

        googleBtn.setOnClickListener(v -> Toast.makeText(this, "Google sign in", Toast.LENGTH_SHORT).show());
        appleBtn.setOnClickListener(v -> Toast.makeText(this, "Apple sign in", Toast.LENGTH_SHORT).show()); // <- changed here
        facebookBtn.setOnClickListener(v -> Toast.makeText(this, "Facebook sign in", Toast.LENGTH_SHORT).show());
    }
}


