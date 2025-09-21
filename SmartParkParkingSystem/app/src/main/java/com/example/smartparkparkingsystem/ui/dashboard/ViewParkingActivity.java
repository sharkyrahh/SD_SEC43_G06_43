package com.example.smartparkparkingsystem.ui.dashboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartparkparkingsystem.R;

import java.util.ArrayList;
import java.util.List;

public class ViewParkingActivity extends AppCompatActivity {

    RecyclerView rvParking;
    List<Parking> parkingList;
    ParkingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_parking);

        rvParking = findViewById(R.id.rvParking);
        rvParking.setLayoutManager(new LinearLayoutManager(this));

        // Dummy data (boleh ganti dengan DB / API)
        parkingList = new ArrayList<>();
        parkingList.add(new Parking("Slot A1", "Available", "-"));
        parkingList.add(new Parking("Slot A2", "Full", "JQB 1234"));
        parkingList.add(new Parking("Slot A3", "Reserved", "WXY 6789"));
        parkingList.add(new Parking("Slot A4", "Available", "-"));

        adapter = new ParkingAdapter(parkingList, this);
        rvParking.setAdapter(adapter);
    }
}

