package com.example.smartparkparkingsystem.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.smartparkparkingsystem.R;
import com.example.smartparkparkingsystem.ReserveActivity;
import com.example.smartparkparkingsystem.StatusActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private TextView welcomeText, availableSlots, totalParking, reservedSlot, reservedDate;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, parkingRef;
    MaterialCardView reserveCard;

    LinearLayout findParking, bookParking;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        welcomeText = view.findViewById(R.id.welcomeText);
        findParking = view.findViewById(R.id.findParking);
        bookParking = view.findViewById(R.id.bookParking);
        availableSlots = view.findViewById(R.id.availableSlots);
        totalParking = view.findViewById(R.id.totalParking);
        reservedSlot = view.findViewById(R.id.reservedSlot);
        reservedDate = view.findViewById(R.id.reservedDate);
        reserveCard = view.findViewById(R.id.reserveCard);

        findParking.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), StatusActivity.class);
            startActivity(intent);
        });

        bookParking.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), ReserveActivity.class);
            startActivity(intent);
        });


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase
                .getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();
        parkingRef = mDatabase.child("Parking");

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            mDatabase.child("users").child(userId).child("fullName")
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().getValue() != null) {
                            String fullName = task.getResult().getValue(String.class);
                            welcomeText.setText("Hi, " + fullName + "!");
                        } else {
                            welcomeText.setText("Hi, User!");
                        }
                    });
        }

        loadParkingCounts();
        loadUserReservation();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        loadParkingCounts();
        loadUserReservation();
    }
    private void loadParkingCounts() {

        parkingRef.child("parkingCount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long totalCount = snapshot.getValue(Long.class);
                totalParking.setText(totalCount != null ? String.valueOf(totalCount) : "0");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                totalParking.setText("0");
            }
        });

        parkingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int availableCount = 0;

                for (DataSnapshot parkingSnapshot : snapshot.getChildren()) {
                    String key = parkingSnapshot.getKey();

                    if ("parkingCount".equals(key)) {
                        continue;
                    }

                    if (parkingSnapshot.hasChild("status")) {
                        String status = parkingSnapshot.child("status").getValue(String.class);
                        if ("Available".equalsIgnoreCase(status)) {
                            availableCount++;
                        }
                    }
                }

                availableSlots.setText(String.valueOf(availableCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                availableSlots.setText("0");
            }
        });
    }

    private void loadUserReservation() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();

        parkingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String reservedSlotName = null;

                for (DataSnapshot parkingSnapshot : snapshot.getChildren()) {
                    String key = parkingSnapshot.getKey();

                    if ("parkingCount".equals(key)) {
                        continue;
                    }

                    if (parkingSnapshot.hasChild("status") && parkingSnapshot.hasChild("reservedby")) {
                        String status = parkingSnapshot.child("status").getValue(String.class);
                        String reservedBy = parkingSnapshot.child("reservedby").getValue(String.class);

                        if ("Reserved".equalsIgnoreCase(status) && userId.equals(reservedBy)) {
                            reservedSlotName = key;
                            break;
                        }
                    }
                }

                if (reservedSlotName != null) {
                    reservedSlot.setText(reservedSlotName);
                    String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                    reservedDate.setText(currentDate);

                    // Use the final variable in the click listener
                    final String finalSlotName = reservedSlotName;
                    reserveCard.setOnClickListener(v -> {
                        new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                                .setTitle("Reserved Slot")
                                .setMessage("Do you want to remove your reservation?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    removeReservation(finalSlotName); // Use the final variable
                                })
                                .setNegativeButton("No", null)
                                .show();
                    });
                } else {
                    reservedSlot.setText("-");
                    reservedDate.setText("No reservation");
                    reserveCard.setOnClickListener(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                reservedSlot.setText("-");
                reservedDate.setText("Error loading");
            }
        });
    }

    private void removeReservation(String slotName) {
        if (mAuth.getCurrentUser() == null) return;

        DatabaseReference slotRef = parkingRef.child(slotName);

        slotRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Object val = currentData.getValue();
                if (val == null) return Transaction.abort();

                if (val instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) val;
                    Object statusObj = map.get("status");
                    Object reservedByObj = map.get("reservedby");

                    String currentStatus = statusObj != null ? statusObj.toString() : "";
                    String currentReservedBy = reservedByObj != null ? reservedByObj.toString() : "";

                    if ("Reserved".equalsIgnoreCase(currentStatus) &&
                            mAuth.getCurrentUser().getUid().equals(currentReservedBy)) {
                        map.put("status", "Available");
                        map.put("reservedby", "");
                        currentData.setValue(map);
                        return Transaction.success(currentData);
                    }
                }
                return Transaction.abort();
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (error != null) {
                    Toast.makeText(requireContext(), "Failed to remove reservation: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (committed) {
                    Toast.makeText(requireContext(), "Reservation removed successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to remove reservation", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (parkingRef != null) {
            parkingRef.removeEventListener((ValueEventListener) null);
        }
    }
}
