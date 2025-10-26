package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParkingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ParkingAdapter adapter;
    private List<Parking> parkingList;
    private ImageView backButton;

    private CardView addParkingBtn;
    private TextView avCount, fulCount, resCount;

    private DatabaseReference parkingRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/");
        parkingRef = database.getReference("Parking");

        recyclerView = findViewById(R.id.rvParking);
        addParkingBtn = findViewById(R.id.addParking);
        backButton = findViewById(R.id.backButton);
        avCount = findViewById(R.id.avCount);
        fulCount = findViewById(R.id.fullCount);
        resCount = findViewById(R.id.resCount);

        parkingList = new ArrayList<>();
        adapter = new ParkingAdapter(parkingList, this, parking -> {

            Intent intent = new Intent(ParkingActivity.this, ViewParkingActivity.class);
            intent.putExtra("parkingName", parking.getName());
            intent.putExtra("parkingStatus", parking.getStatus());
            intent.putExtra("parkingLocation", parking.getLocation());
            intent.putExtra("parkingType", parking.getType());
            intent.putExtra("reservedBy", parking.getReservedby());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fetchParkingData();

        addParkingBtn.setOnClickListener(v -> {
            startActivity(new Intent(ParkingActivity.this, AddParkingActivity.class));
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void fetchParkingData() {
        parkingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                parkingList.clear();
                int availableCount = 0, fullCount = 0, reservedCount = 0;

                for (DataSnapshot parkingSpotSnapshot : snapshot.getChildren()) {
                    String parkingSpotName = parkingSpotSnapshot.getKey();

                    if (parkingSpotSnapshot.hasChild("status")) {

                        Parking parking = new Parking();
                        parking.setName(parkingSpotName);
                        parking.setLocation(getStringValue(parkingSpotSnapshot, "location"));
                        parking.setStatus(getStringValue(parkingSpotSnapshot, "status"));
                        parking.setReservedby(getStringValue(parkingSpotSnapshot, "reservedby"));
                        parking.setType(getStringValue(parkingSpotSnapshot, "type"));

                        parkingList.add(parking);

                        String status = parking.getStatus();
                        if (status != null) {
                            if ("Available".equalsIgnoreCase(status)) {
                                availableCount++;
                            } else if ("Full".equalsIgnoreCase(status)) {
                                fullCount++;
                            } else if ("Reserved".equalsIgnoreCase(status)) {
                                reservedCount++;
                            }
                        }
                    } else {

                        System.out.println("Skipping invalid parking data: " + parkingSpotName);
                    }
                }

                adapter.notifyDataSetChanged();
                avCount.setText(String.valueOf(availableCount));
                fulCount.setText(String.valueOf(fullCount));
                resCount.setText(String.valueOf(reservedCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ParkingActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
}