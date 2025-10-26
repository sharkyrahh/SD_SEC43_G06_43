package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartparkparkingsystem.MainActivity;
import com.example.smartparkparkingsystem.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.TextView;
import android.widget.Toast;

public class DashboardFragment extends Fragment {

    TextView userCount, slotsCount;
    DatabaseReference usersRef, parkingRef;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/");
        usersRef = database.getReference("users");
        parkingRef = database.getReference("Parking");

        userCount = view.findViewById(R.id.userCount);
        slotsCount = view.findViewById(R.id.slotsCount);

        loadUserCount();
        loadParkingCount();

        MaterialCardView cardViewUser = view.findViewById(R.id.card_view_user);
        MaterialCardView cardViewProfile = view.findViewById(R.id.card_view_profile);
        MaterialCardView cardViewParking = view.findViewById(R.id.card_parking);
        MaterialCardView cardLogs = view.findViewById(R.id.card_logs);
        MaterialCardView cardLogout = view.findViewById(R.id.card_logout);
        userCount = view.findViewById(R.id.userCount);
        slotsCount = view.findViewById(R.id.slotsCount);

        cardViewUser.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), UserListActivity.class));
        });

        cardViewParking.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ParkingActivity.class));
        });

        cardLogs.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), HistoryActivity.class));
        });

        cardViewProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), Adminprofile.class));
        });


        cardLogout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Toast.makeText(getContext(), "Logout successful", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }

    private void loadUserCount() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                userCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userCount.setText("0");
            }
        });
    }

    private void loadParkingCount() {
        parkingRef.child("parkingCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long count = snapshot.getValue(Long.class);
                slotsCount.setText(count != null ? String.valueOf(count) : "0");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                slotsCount.setText("0");
            }
        });
    }
}