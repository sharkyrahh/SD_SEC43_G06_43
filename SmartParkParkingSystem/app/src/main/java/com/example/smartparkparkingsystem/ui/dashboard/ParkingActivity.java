package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartparkparkingsystem.R;

import java.util.ArrayList;
import java.util.List;

public class ParkingActivity extends AppCompatActivity {

    RecyclerView rvParking;
    List<Parking> parkingList;
    ParkingAdapter adapter;

    ImageView backButton;

    CardView addParking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        backButton = findViewById(R.id.backButton);
        addParking = findViewById(R.id.addParking);
        rvParking = findViewById(R.id.rvParking);
        rvParking.setLayoutManager(new LinearLayoutManager(this));

        backButton.setOnClickListener(v -> finish());
        addParking.setOnClickListener(v-> {
            Intent intent = new Intent(ParkingActivity.this, AddParkingActivity.class);
            startActivity(intent);
        });

        // Dummy data (boleh ganti dengan DB / API)
        parkingList = new ArrayList<>();
        parkingList.add(new Parking("Slot A1", "Available", "-"));
        parkingList.add(new Parking("Slot A2", "Full", "JQB 1234"));
        parkingList.add(new Parking("Slot A3", "Reserved", "WXY 6789"));
        parkingList.add(new Parking("Slot A4", "Available", "-"));
        parkingList.add(new Parking("Slot A5", "Available", "-"));
        parkingList.add(new Parking("Slot A6", "Full", "JQB 1234"));
        parkingList.add(new Parking("Slot A7", "Reserved", "WXY 6789"));
        parkingList.add(new Parking("Slot A8", "Available", "-"));

        adapter = new ParkingAdapter(parkingList, this, new ParkingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Parking parking) {
                Intent intent = new Intent(ParkingActivity.this, ViewParkingActivity.class);
                /*intent.putExtra("slotName", parking.getSlotName());
                intent.putExtra("status", parking.getStatus());
                intent.putExtra("car", parking.getCar());*/
                startActivity(intent);
            }
        });
        rvParking.setAdapter(adapter);
    }

}

