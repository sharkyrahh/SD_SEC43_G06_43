package com.example.smartparkparkingsystem;



import static android.app.ProgressDialog.show;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.content.Intent;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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

        matricCardEditText.setFilters(new InputFilter[]{
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(9)
        });

        plateNumberEditText.setFilters(new InputFilter[]{
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(10)
        });

        programCodeEditText.setFilters(new InputFilter[]{
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(4)
        });


        loginLabel.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, MainActivity.class);
            startActivity(intent);
        });

        facultyEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(SignUp.this, facultyEditText);
                popupMenu.getMenuInflater().inflate(R.menu.facultymenu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        facultyEditText.setText(item.getTitle());
                        return true;
                    }
                });

                popupMenu.show();
            }
        });


        signUpButton.setOnClickListener(v -> {
            String name = fullNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String matricCard = matricCardEditText.getText().toString().trim();
            String faculty = facultyEditText.getText().toString().trim();
            String plateNumber = plateNumberEditText.getText().toString().trim();
            String programCode = programCodeEditText.getText().toString().trim();


            if (name.isEmpty() && email.isEmpty() && password.isEmpty() && confirmPassword.isEmpty()
                    && matricCard.isEmpty() && faculty.isEmpty() && plateNumber.isEmpty() && programCode.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (name.isEmpty()) {
                Toast.makeText(this, "Please fill in your name.", Toast.LENGTH_SHORT).show();
                fullNameEditText.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                Toast.makeText(this, "Please fill in your email.", Toast.LENGTH_SHORT).show();
                emailEditText.requestFocus();
                return;
            }

            if (!email.contains("@")) {
                emailEditText.setError("Missing '@'");
                emailEditText.requestFocus();
                return;
            }

            if (!email.contains(".com") && !email.contains(".my")) {
                emailEditText.setError("Missing '.com'");
                emailEditText.requestFocus();
                return;
            }


            int atCount = email.length() - email.replace("@", "").length();

            if (atCount > 1) {
                emailEditText.setError("Too many '@'s");
                emailEditText.requestFocus();
                return;
            }

            if (email.lastIndexOf(".") < email.indexOf("@")) {
                emailEditText.setError("Invalid email format");
                emailEditText.requestFocus();
                return;
            }

            if (email.indexOf(".") - email.indexOf("@") <= 1) {
                emailEditText.setError("Invalid email format");
                emailEditText.requestFocus();
                return;
            }


            if (password.isEmpty()) {
                Toast.makeText(this, "Please fill in your password.", Toast.LENGTH_SHORT).show();
                passwordEditText.requestFocus();
                return;
            }

            if (confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please confirm your password.", Toast.LENGTH_SHORT).show();
                confirmPasswordEditText.requestFocus();
                return;
            }

            if (matricCard.isEmpty()) {
                Toast.makeText(this, "Please fill in your matric number.", Toast.LENGTH_SHORT).show();
                matricCardEditText.requestFocus();
                return;
            }

            if (matricCard.length() != 9) {
                matricCardEditText.setError("Invalid matric card.");
                matricCardEditText.requestFocus();
            }

            if (faculty.isEmpty()) {
                Toast.makeText(this, "Please select faculty.", Toast.LENGTH_SHORT).show();
                return;
            }


            if (plateNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in your plate number.", Toast.LENGTH_SHORT).show();
                plateNumberEditText.requestFocus();
                return;
            }

            if (programCode.isEmpty()) {
                Toast.makeText(this, "Please fill in your program code.", Toast.LENGTH_SHORT).show();
                programCodeEditText.requestFocus();
                return;
            }

            if (programCode.length() != 4) {
                programCodeEditText.setError("Invalid program code.");
                programCodeEditText.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                confirmPasswordEditText.setError("Passwords do not match");
                confirmPasswordEditText.requestFocus();
                return;
            }

            if (password.length() < 8) {
                passwordEditText.setError("Passwords must be at least 8 characters");
                passwordEditText.requestFocus();
                return;
            }

            if (!password.matches(".*[A-Z].*")) {
                passwordEditText.setError("Passwords must must contain at least one uppercase letter");
                passwordEditText.requestFocus();
                return;
            }

            if (!password.matches(".*[!@#$%^&*+=?-].*")) {
                passwordEditText.setError("Passwords must must contain at least one symbol (!@#$%^&*+=?-)");
                passwordEditText.requestFocus();
                return;
            }


            if (!(name.isEmpty() && email.isEmpty() && password.isEmpty() && confirmPassword.isEmpty() && matricCard.isEmpty() && faculty.isEmpty() && plateNumber.isEmpty() && programCode.isEmpty())) {
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
            }
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