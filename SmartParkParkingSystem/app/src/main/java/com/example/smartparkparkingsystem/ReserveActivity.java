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

    private RecyclerView rv;
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

        rv = findViewById(R.id.rvReserveSlots);
        rv.setLayoutManager(new GridLayoutManager(this, 2));

        // initialize adapter BEFORE loading data
        adapter = new slotsAdapter(this, list, slot -> handleSlotClick(slot));
        rv.setAdapter(adapter);

        // try Firebase init
        try {
            slotsRef = FirebaseDatabase.getInstance().getReference("parking_slots");
            reservationsRef = FirebaseDatabase.getInstance().getReference("reservations");
            auth = FirebaseAuth.getInstance();
            loadSlotsFromFirebase();
        } catch (Exception ex) {
            ex.printStackTrace();
            populateSampleSlots();
        }
    }

    private void loadSlotsFromFirebase() {
        if (slotsRef == null) {
            populateSampleSlots();
            return;
        }

        slotsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot c : snapshot.getChildren()) {
                    ParkingSlot s = c.getValue(ParkingSlot.class);
                    if (s != null) {
                        s.setId(c.getKey());
                        list.add(s);
                    }
                }
                adapter.notifyDataSetChanged(); // adapter already initialized
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReserveActivity.this, "Failed to load slots: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                if (list.isEmpty()) populateSampleSlots();
            }
        });
    }

    private void populateSampleSlots() {
        list.clear();
        list.add(new ParkingSlot("1", "A1", "Front", "available"));
        list.add(new ParkingSlot("2", "A2", "Front", "reserved"));
        list.add(new ParkingSlot("3", "B1", "Back", "occupied"));
        list.add(new ParkingSlot("4", "B2", "Back", "available"));
        list.add(new ParkingSlot("5", "C1", "Side", "available"));
        list.add(new ParkingSlot("6", "C2", "Side", "reserved"));
        adapter.notifyDataSetChanged();
    }

    private void handleSlotClick(ParkingSlot slot) {
        if (slot == null) return;
        String status = slot.getStatus() == null ? "unknown" : slot.getStatus().toLowerCase();
        if ("available".equalsIgnoreCase(status)) {
            new AlertDialog.Builder(this)
                    .setTitle("Reserve " + (slot.getCode() != null ? slot.getCode() : "slot"))
                    .setMessage("Do you want to reserve this slot?")
                    .setPositiveButton("Reserve", (dialog, which) -> attemptReserve(slot))
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            Toast.makeText(this, "Slot " + (slot.getCode() != null ? slot.getCode() : "") + " is " + status, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("unchecked")
    private void attemptReserve(ParkingSlot slot) {
        if (slotsRef == null || reservationsRef == null) {
            slot.setStatus("reserved");
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Reserved (local) " + slot.getCode(), Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please sign in to reserve (local fallback)", Toast.LENGTH_SHORT).show();
            slot.setStatus("reserved");
            adapter.notifyDataSetChanged();
            return;
        }

        String uid = user.getUid();
        DatabaseReference slotRef = slotsRef.child(slot.getId());

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
                String curStatus = sObj == null ? "available" : sObj.toString();
                if (!"available".equalsIgnoreCase(curStatus)) return Transaction.abort();
                map.put("status", "reserved");
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
                r.put("slotId", slot.getId());
                r.put("userId", uid);
                r.put("startTime", now);
                r.put("endTime", now + 30 * 60 * 1000L);
                r.put("status", "active");

                reservationsRef.child(resId).setValue(r)
                        .addOnSuccessListener(aVoid -> Toast.makeText(ReserveActivity.this, "Reserved " + slot.getCode(), Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> {
                            Toast.makeText(ReserveActivity.this, "Save reservation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            slotsRef.child(slot.getId()).child("status").setValue("available");
                        });
            }
        });
    }
}
