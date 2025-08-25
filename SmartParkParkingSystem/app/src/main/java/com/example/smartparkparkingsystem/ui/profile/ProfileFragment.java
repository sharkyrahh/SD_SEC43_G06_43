package com.example.smartparkparkingsystem.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView fullNameText, courseText, matricNumberText, programmeCodeText, facultyText, primaryEmailText, plateNumberText, userIdText;
    private Button editBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Database with your project URL
        mDatabase = FirebaseDatabase.getInstance(
                        "https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users");

        // Initialize Views
        fullNameText = view.findViewById(R.id.fullNameText);
        matricNumberText = view.findViewById(R.id.matricNumberText);
        programmeCodeText = view.findViewById(R.id.programmeCodeText);
        facultyText = view.findViewById(R.id.facultyText);
        primaryEmailText = view.findViewById(R.id.primaryEmailText);
        plateNumberText = view.findViewById(R.id.secondaryEmailText); // reuse XML id for plate number

        editBtn = view.findViewById(R.id.editBtn);

        // Set Edit button action
        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        loadProfileData();

        return view;
    }

    private void loadProfileData() {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();

            mDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Retrieve values from database
                        String fullName = snapshot.child("fullName").getValue(String.class);
                        String matricCard = snapshot.child("matricCard").getValue(String.class);
                        String programCode = snapshot.child("programCode").getValue(String.class);
                        String faculty = snapshot.child("faculty").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String plateNumber = snapshot.child("plateNumber").getValue(String.class);

                        // Set values to TextViews
                        fullNameText.setText(fullName != null ? fullName : "N/A");
                        matricNumberText.setText(matricCard != null ? matricCard : "N/A");
                        programmeCodeText.setText(programCode != null ? programCode : "N/A");
                        facultyText.setText(faculty != null ? faculty : "N/A");
                        primaryEmailText.setText(email != null ? email : "N/A");
                        plateNumberText.setText(plateNumber != null ? plateNumber : "N/A");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
    }

}
