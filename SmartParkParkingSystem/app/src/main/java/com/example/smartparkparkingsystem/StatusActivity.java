package com.example.smartparkparkingsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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

        backButton = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.rvSlots);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        backButton.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        slotList = new ArrayList<>();
        adapter = new slotsAdapter(this, slotList, this);
        recyclerView.setAdapter(adapter);

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
                        if (snapshot.hasChild("status")) {
                            ParkingSlot slot = snapshot.getValue(ParkingSlot.class);
                            if (slot != null) {
                                if (slot.getName() == null || slot.getName().isEmpty()) {
                                    slot.setName(snapshot.getKey());
                                }
                                slotList.add(slot);
                            }
                        } else {
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
    private void loadParkingSlotsManually() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = database.getReference("Parking");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                slotList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String slotName = snapshot.getKey();

                    if (snapshot.hasChild("status")) {
                        String name = slotName;
                        String location = getStringValue(snapshot, "location");
                        String status = getStringValue(snapshot, "status");
                        String type = getStringValue(snapshot, "type");
                        String reservedby = getStringValue(snapshot, "reservedby");

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
    private String getStringValue(DataSnapshot snapshot, String key) {
        if (snapshot.hasChild(key)) {
            Object value = snapshot.child(key).getValue();
            if (value != null) {
                return value.toString();
            }
        }
        return "";
    }

    @Override
    public void onSlotClick(ParkingSlot slot) {
        Toast.makeText(this,
                "Slot " + slot.getName() + " clicked - Status: " + slot.getStatus(),
                Toast.LENGTH_SHORT).show();
    }
}