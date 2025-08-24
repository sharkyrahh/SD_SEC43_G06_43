package com.example.smartparkparkingsystem;



import android.graphics.Paint;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    EditText fullNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    EditText matricCardEditText, facultyEditText, plateNumberEditText, programCodeEditText;
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
        mDatabase = FirebaseDatabase
                .getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();


        loginLabel = findViewById(R.id.loginLabel);
        loginLabel.setPaintFlags(loginLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        matricCardEditText = findViewById(R.id.matricCardEditText);
        facultyEditText = findViewById(R.id.facultyEditText);
        plateNumberEditText = findViewById(R.id.plateNumberEditText);
        programCodeEditText = findViewById(R.id.programCodeEditText);
        signUpButton = findViewById(R.id.signupButton);


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
            String matricCard = matricCardEditText.getText().toString().trim();
            String faculty = facultyEditText.getText().toString().trim();
            String plateNumber = plateNumberEditText.getText().toString().trim();
            String programCode = programCodeEditText.getText().toString().trim();


            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                    || matricCard.isEmpty() || faculty.isEmpty() || plateNumber.isEmpty() || programCode.isEmpty()) {
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

            if (!password.matches(".*[A-Z].*")) {
                Toast.makeText(this, "Password must contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.matches(".*[!@#$%^&*+=?-].*")) {
                Toast.makeText(this, "Password must contain at least one symbol (!@#$%^&*+=?-)", Toast.LENGTH_SHORT).show();
                return;
            }



            // Firebase Auth SignUp
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                if (firebaseUser != null) {
                                    String userId = firebaseUser.getUid();

                                    User newUser = new User(name, email, userId, matricCard, faculty, plateNumber, programCode);
                                    mDatabase.child("users").child(userId).setValue(newUser);

                                    firebaseUser.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SignUp.this,
                                                                "Sign up successful! Please verify your email.",
                                                                Toast.LENGTH_LONG).show();
                                                        // Move to VerifyEmailActivity
                                                        Intent intent = new Intent(SignUp.this, VerifyEmailActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(SignUp.this,
                                        "Sign up failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }


    // Model class
    public static class User {
        public String fullName;
        public String email;
        public String userId;
        public String matricCard;
        public String faculty;
        public String plateNumber;
        public String programCode;

        public User() {
        }

        public User(String fullName, String email, String userId,
                    String matricCard, String faculty, String plateNumber, String programCode) {
            this.fullName = fullName;
            this.email = email;
            this.userId = userId;
            this.matricCard = matricCard;
            this.faculty = faculty;
            this.plateNumber = plateNumber;
            this.programCode = programCode;
        }
    }



}