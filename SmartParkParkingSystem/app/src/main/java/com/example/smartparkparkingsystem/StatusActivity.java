package com.example.smartparkparkingsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StatusActivity extends AppCompatActivity implements slotsAdapter.Listener {

    private RecyclerView recyclerView;
    private slotsAdapter adapter;
    private List<ParkingSlot> slotList;
    private DatabaseReference databaseReference;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // Initialize views
        backButton = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.rvSlots);

        // Setup back button
        backButton.setOnClickListener(v -> finish());

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize list and adapter
        slotList = new ArrayList<>();
        adapter = new slotsAdapter(this, slotList, this);
        recyclerView.setAdapter(adapter);

        // Load data from Firebase
        loadParkingSlotsFromFirebase();
    }

    private void loadParkingSlotsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = database.getReference("Parking");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                slotList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        // Check if this is a valid ParkingSlot object (has status field)
                        if (snapshot.hasChild("status")) {
                            ParkingSlot slot = snapshot.getValue(ParkingSlot.class);
                            if (slot != null) {
                                // Set the name from Firebase key if needed
                                if (slot.getName() == null || slot.getName().isEmpty()) {
                                    slot.setName(snapshot.getKey());
                                }
                                slotList.add(slot);
                            }
                        } else {
                            // Skip non-ParkingSlot objects (like Long values)
                            System.out.println("Skipping non-ParkingSlot data: " + snapshot.getKey() + " = " + snapshot.getValue());
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing parking slot " + snapshot.getKey() + ": " + e.getMessage());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StatusActivity.this,
                        "Failed to load parking slots: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Alternative method: Manual field mapping (more robust)
    private void loadParkingSlotsManually() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = database.getReference("Parking");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                slotList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String slotName = snapshot.getKey();

                    // Only process if it has the structure of a ParkingSlot
                    if (snapshot.hasChild("status")) {
                        // Manually get each field to avoid conversion errors
                        String name = slotName;
                        String location = getStringValue(snapshot, "location");
                        String status = getStringValue(snapshot, "status");
                        String type = getStringValue(snapshot, "type");
                        String reservedby = getStringValue(snapshot, "reservedby");

                        // Create ParkingSlot object manually
                        ParkingSlot slot = new ParkingSlot();
                        slot.setName(name);
                        slot.setLocation(location);
                        slot.setStatus(status);
                        slot.setType(type);
                        slot.setReservedby(reservedby);

                        slotList.add(slot);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StatusActivity.this,
                        "Failed to load parking slots: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to safely get string values from Firebase
    private String getStringValue(DataSnapshot snapshot, String key) {
        if (snapshot.hasChild(key)) {
            Object value = snapshot.child(key).getValue();
            if (value != null) {
                return value.toString();
            }
        }
        return "";
    }

    // This method comes from slotsAdapter.Listener interface
    @Override
    public void onSlotClick(ParkingSlot slot) {
        // Handle when a parking slot is clicked
        Toast.makeText(this,
                "Slot " + slot.getName() + " clicked - Status: " + slot.getStatus(),
                Toast.LENGTH_SHORT).show();
    }
}