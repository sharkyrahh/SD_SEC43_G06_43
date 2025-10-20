package com.example.smartparkparkingsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
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
        recyclerView = findViewById(R.id.rvSlots); // Match your XML id

        // Setup back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close this activity and go back
            }
        });

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize list
        slotList = new ArrayList<>();

        // Initialize adapter
        adapter = new slotsAdapter(this, slotList, this);
        recyclerView.setAdapter(adapter);

        // Load data from Firebase
        loadParkingSlotsFromFirebase();
    }

    private void loadParkingSlotsFromFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("parking_slots");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                slotList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ParkingSlot slot = snapshot.getValue(ParkingSlot.class);
                    if (slot != null) {
                        slot.setId(snapshot.getKey()); // Set Firebase key as ID
                        slotList.add(slot);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(StatusActivity.this,
                        "Failed to load parking slots: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // This method comes from slotsAdapter.Listener interface
    @Override
    public void onSlotClick(ParkingSlot slot) {
        // Handle when a parking slot is clicked
        Toast.makeText(this,
                "Slot " + slot.getCode() + " clicked - Status: " + slot.getStatus(),
                Toast.LENGTH_SHORT).show();

        // You can add reservation logic here
        // openReservationDialog(slot);
    }
}