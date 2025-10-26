package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.smartparkparkingsystem.R;
import com.example.smartparkparkingsystem.ui.profile.EditProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Adminprofile extends AppCompatActivity {

    private TextView adminNameText, courseText, Adminemail, phonenum, primaryEmailText, secondaryEmailText;
    private DatabaseReference roleRef;

    Button editBtn;

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_profile);

        adminNameText = findViewById(R.id.adminNameText);
        courseText = findViewById(R.id.courseText);
        Adminemail = findViewById(R.id.Adminemail);
        phonenum = findViewById(R.id.phonenum);
        primaryEmailText = findViewById(R.id.primaryEmailText);
        secondaryEmailText = findViewById(R.id.secondaryEmailText);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        editBtn = findViewById(R.id.editBtn);

        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Adminprofile.this, Editprofile.class);
            startActivity(intent);
        });

        roleRef = FirebaseDatabase.getInstance(
                "https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("Role");

        loadAdminProfile();
    }

    private void loadAdminProfile() {
        roleRef.orderByChild("role").equalTo("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String fullname = child.child("fullname").getValue(String.class);
                        String role = child.child("role").getValue(String.class);
                        String email = child.child("email").getValue(String.class);
                        String phone = child.child("Phonenumber").getValue(String.class);
                        String gender = child.child("Gender").getValue(String.class);


                        String employeeId = "";
                        Object empIdObj = child.child("employeeid").getValue();
                        if (empIdObj != null) {
                            employeeId = String.valueOf(empIdObj);
                        }

                        adminNameText.setText(fullname != null ? fullname : "N/A");
                        courseText.setText(role != null ? role : "N/A");
                        Adminemail.setText(email != null ? email : "N/A");
                        phonenum.setText(phone != null ? phone : "N/A");
                        primaryEmailText.setText(gender != null ? gender : "N/A");
                        secondaryEmailText.setText(employeeId);
                    }
                } else {
                    Toast.makeText(Adminprofile.this, "No admin record found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Adminprofile.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
