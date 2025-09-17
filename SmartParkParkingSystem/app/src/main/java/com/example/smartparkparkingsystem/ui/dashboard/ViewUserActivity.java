package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewUserActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView fullNameText, matricNumberText, programmeCodeText, facultyText, primaryEmailText, secondaryEmailText;
    private Button editBtn;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewuser);

        backButton = findViewById(R.id.backButton);
        fullNameText = findViewById(R.id.fullNameText);
        matricNumberText = findViewById(R.id.matricNumberText);
        programmeCodeText = findViewById(R.id.programmeCodeText);
        facultyText = findViewById(R.id.facultyText);
        primaryEmailText = findViewById(R.id.primaryEmailText);
        secondaryEmailText = findViewById(R.id.secondaryEmailText);
        editBtn = findViewById(R.id.editBtn);

        backButton.setOnClickListener(v -> finish());

        userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            loadUserData(userId);
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
        }

        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ViewUserActivity.this, EditUserActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    private void loadUserData(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String matricCard = snapshot.child("matricCard").getValue(String.class);
                    String programCode = snapshot.child("programCode").getValue(String.class);
                    String faculty = snapshot.child("faculty").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String plateNumber = snapshot.child("plateNumber").getValue(String.class);

                    fullNameText.setText(fullName != null ? fullName : "");
                    matricNumberText.setText(matricCard != null ? matricCard : "");
                    programmeCodeText.setText(programCode != null ? programCode : "");
                    facultyText.setText(faculty != null ? faculty : "");
                    primaryEmailText.setText(email != null ? email : "");
                    secondaryEmailText.setText(plateNumber != null ? plateNumber : "");
                } else {
                    Toast.makeText(ViewUserActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ViewUserActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
