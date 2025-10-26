package com.example.smartparkparkingsystem;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReserveActivity extends AppCompatActivity {

    private slotsAdapter adapter;
    private final List<ParkingSlot> list = new ArrayList<>();

    private DatabaseReference slotsRef;
    private DatabaseReference reservationsRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        ImageView back = findViewById(R.id.backButton);
        if (back != null) back.setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvReserveSlots);
        rv.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new slotsAdapter(this, list, this::handleSlotClick);
        rv.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/");
        slotsRef = database.getReference("Parking");
        reservationsRef = database.getReference("reservations");
        auth = FirebaseAuth.getInstance();

        loadSlotsFromFirebase();
    }

    private void loadSlotsFromFirebase() {
        slotsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot c : snapshot.getChildren()) {
                    try {
                        if (c.hasChild("status")) {
                            ParkingSlot s = c.getValue(ParkingSlot.class);
                            if (s != null) {
                                if (s.getName() == null || s.getName().isEmpty()) {
                                    s.setName(c.getKey());
                                }
                                list.add(s);
                            }
                        } else {
                            System.out.println("Skipping non-ParkingSlot data: " + c.getKey() + " = " + c.getValue());
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing parking slot " + c.getKey() + ": " + e.getMessage());
                    }
                }
                adapter.notifyDataSetChanged();

                if (list.isEmpty()) {
                    Toast.makeText(ReserveActivity.this, "No parking slots found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReserveActivity.this, "Failed to load slots: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSlotClick(ParkingSlot slot) {
        if (slot == null) return;
        String status = slot.getStatus() == null ? "unknown" : slot.getStatus().toLowerCase();
        if ("available".equalsIgnoreCase(status)) {
            new AlertDialog.Builder(this)
                    .setTitle("Reserve " + (slot.getName() != null ? slot.getName() : "slot"))
                    .setMessage("Do you want to reserve this slot?")
                    .setPositiveButton("Reserve", (dialog, which) -> attemptReserve(slot))
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            Toast.makeText(this, "Slot " + (slot.getName() != null ? slot.getName() : "") + " is " + status, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("unchecked")
    private void attemptReserve(ParkingSlot slot) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please sign in to reserve", Toast.LENGTH_SHORT).show();
            return;
        }

        String slotName = slot.getName();
        if (slotName == null) {
            Toast.makeText(this, "Invalid slot", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference slotRef = slotsRef.child(slotName);

        slotRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Object val = currentData.getValue();
                if (val == null) return Transaction.abort();
                Map<String, Object> map;
                if (val instanceof Map) {
                    map = (Map<String, Object>) val;
                } else {
                    return Transaction.abort();
                }
                Object sObj = map.get("status");
                String curStatus = sObj == null ? "Available" : sObj.toString();
                if (!"available".equalsIgnoreCase(curStatus)) return Transaction.abort();

                map.put("status", "Reserved");
                map.put("reservedby", user.getUid());
                currentData.setValue(map);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (error != null) {
                    Toast.makeText(ReserveActivity.this, "Reserve failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!committed) {
                    Toast.makeText(ReserveActivity.this, "Slot no longer available", Toast.LENGTH_SHORT).show();
                    return;
                }

                String resId = reservationsRef.push().getKey();
                long now = System.currentTimeMillis();
                Map<String, Object> r = new HashMap<>();
                r.put("id", resId);
                r.put("slotName", slotName);
                r.put("userId", user.getUid());
                r.put("startTime", now);
                r.put("endTime", now + 30 * 60 * 1000L);
                r.put("status", "active");

                reservationsRef.child(resId).setValue(r)
                        .addOnSuccessListener(aVoid -> Toast.makeText(ReserveActivity.this, "Reserved " + slotName, Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> {
                            Toast.makeText(ReserveActivity.this, "Save reservation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            slotsRef.child(slotName).child("status").setValue("Available");
                            slotsRef.child(slotName).child("reservedby").removeValue();
                        });
            }
        });
    }
}