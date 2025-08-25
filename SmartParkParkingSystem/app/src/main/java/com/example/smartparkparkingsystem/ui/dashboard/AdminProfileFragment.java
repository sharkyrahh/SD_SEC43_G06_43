package com.example.smartparkparkingsystem.ui.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AdminProfileFragment extends Fragment {

    private TextView adminNameText, courseText, Adminemail, phonenum, primaryEmailText, secondaryEmailText;
    private DatabaseReference roleRef;
    private ImageView backButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);

        adminNameText = view.findViewById(R.id.adminNameText);
        courseText = view.findViewById(R.id.courseText);
        Adminemail = view.findViewById(R.id.Adminemail);
        phonenum = view.findViewById(R.id.phonenum);
        primaryEmailText = view.findViewById(R.id.primaryEmailText);
        secondaryEmailText = view.findViewById(R.id.secondaryEmailText);
        backButton = view.findViewById(R.id.backButton);

        // Set click listener for back button
        backButton.setOnClickListener(v -> {
            // Go back to previous fragment
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Initialize Database with your project URL
        roleRef = FirebaseDatabase.getInstance(
                "https://utm-smartparkparkingsystem-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("Role");

        loadAdminProfile();

        return view;
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

                        // Handle employeeid safely (could be Long or String)
                        String employeeId = "";
                        Object empIdObj = child.child("employeeid").getValue();
                        if (empIdObj != null) {
                            employeeId = String.valueOf(empIdObj);
                        }

                        // Set values to UI
                        adminNameText.setText(fullname != null ? fullname : "N/A");
                        courseText.setText(role != null ? role : "N/A");
                        Adminemail.setText(email != null ? email : "N/A");
                        phonenum.setText(phone != null ? phone : "N/A");
                        primaryEmailText.setText(gender != null ? gender : "N/A");
                        secondaryEmailText.setText(employeeId);
                    }
                } else {
                    Toast.makeText(getActivity(), "No admin record found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
