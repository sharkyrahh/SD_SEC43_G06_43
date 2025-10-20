package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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
    private TextView tvAvailable, tvFull, tvReserved;

    private DatabaseReference parkingRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database with your URL
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/");
        parkingRef = database.getReference("Parking");

        // Views
        recyclerView = findViewById(R.id.rvParking);
        addParkingBtn = findViewById(R.id.addParking);
        backButton = findViewById(R.id.backButton);
        tvAvailable = findViewById(R.id.tvAvailable);
        tvFull = findViewById(R.id.tvFull);
        tvReserved = findViewById(R.id.tvReserved);

        // RecyclerView setup
        parkingList = new ArrayList<>();
        adapter = new ParkingAdapter(parkingList, this, parking -> {
            // Pass the Name to ViewParkingActivity
            Intent intent = new Intent(ParkingActivity.this, ViewParkingActivity.class);
            intent.putExtra("Name", parking.getName()); // key matches database field
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Firebase reference
        parkingRef = FirebaseDatabase.getInstance().getReference("Parking");

        // Fetch data
        fetchParkingData();

        // Add parking button click
        addParkingBtn.setOnClickListener(v -> {
            startActivity(new Intent(ParkingActivity.this, AddParkingActivity.class));
        });

        // Back button
        backButton.setOnClickListener(v -> finish());
    }

    private void fetchParkingData() {
        parkingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                parkingList.clear();
                int availableCount = 0, fullCount = 0, reservedCount = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Parking parking = ds.getValue(Parking.class);
                    if (parking != null) {
                        parkingList.add(parking);

                        // Status counts
                        String status = parking.getStatus(); // Available, Full, Reserved
                        if ("Available".equalsIgnoreCase(status)) availableCount++;
                        else if ("Full".equalsIgnoreCase(status)) fullCount++;
                        else if ("Reserved".equalsIgnoreCase(status)) reservedCount++;
                    }
                }

                adapter.notifyDataSetChanged();

                tvAvailable.setText("Available: " + availableCount);
                tvFull.setText("Full: " + fullCount);
                tvReserved.setText("Reserved: " + reservedCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });
    }
}
