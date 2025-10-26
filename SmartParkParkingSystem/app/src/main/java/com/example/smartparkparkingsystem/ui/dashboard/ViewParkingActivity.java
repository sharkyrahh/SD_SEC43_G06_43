package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewParkingActivity extends AppCompatActivity {

    private ImageView backButton;
    private Button editBtn;
    private ImageButton delBtn;

    private TextView parkingName, parkingStatus, parkingLocation, parkingType, reserveText, occupyText;

    private DatabaseReference parkingRef, usersRef;
    private String currentParkingName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_parking);

        backButton = findViewById(R.id.backButton);
        editBtn = findViewById(R.id.editBtn);
        delBtn = findViewById(R.id.delBtn);

        parkingName = findViewById(R.id.parkingName);
        parkingStatus = findViewById(R.id.parkingStatus);
        parkingLocation = findViewById(R.id.parkingLocation);
        parkingType = findViewById(R.id.parkingType);
        reserveText = findViewById(R.id.reserveText);
        occupyText = findViewById(R.id.occupyText);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/");
        parkingRef = database.getReference("Parking");
        usersRef = database.getReference("users");

        Intent intent = getIntent();
        currentParkingName = intent.getStringExtra("parkingName");

        if (currentParkingName != null) {
            loadParkingDataFromFirebase(currentParkingName);
        } else {
            Toast.makeText(this, "Error: No parking slot specified", Toast.LENGTH_SHORT).show();
            finish();
        }

        backButton.setOnClickListener(v -> finish());

        editBtn.setOnClickListener(v -> {
            Intent editIntent = new Intent(ViewParkingActivity.this, EditParkingActivity.class);
            editIntent.putExtra("parkingName", currentParkingName);
            editIntent.putExtra("parkingStatus", parkingStatus.getText().toString());
            editIntent.putExtra("parkingLocation", parkingLocation.getText().toString());
            editIntent.putExtra("parkingType", parkingType.getText().toString());
            editIntent.putExtra("reservedBy", reserveText.getText().toString());
            startActivity(editIntent);
        });

        delBtn.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Parking Slot")
                    .setMessage("Are you sure you want to delete this parking slot?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (currentParkingName != null) {
                            deleteParkingSlot();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void loadParkingDataFromFirebase(String parkingName) {
        parkingRef.child(parkingName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String name = parkingName;
                    String location = getStringValue(snapshot, "location");
                    String status = getStringValue(snapshot, "status");
                    String type = getStringValue(snapshot, "type");
                    String reservedBy = getStringValue(snapshot, "reservedby");

                    if("Full".equalsIgnoreCase(status)){
                        occupyText.setText("Occupied By:");
                    } else {
                        occupyText.setText("Reserved By:");
                    }

                    if (reservedBy != null && !reservedBy.isEmpty()) {
                        loadPlateNumber(reservedBy, name, location, status, type);
                    } else {
                    updateUI(name, location, status, type, "N/A");}
                } else {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ViewParkingActivity.this,
                        "Failed to load parking data: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadPlateNumber(String userId, String name, String location, String status, String type) {
        usersRef.child(userId).child("plateNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String plateNumber = snapshot.getValue(String.class);
                if (plateNumber != null && !plateNumber.isEmpty()) {
                    updateUI(name, location, status, type, plateNumber);
                } else {
                    updateUI(name, location, status, type, "N/A");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                updateUI(name, location, status, type, "N/A");
            }
        });
    }

    private void updateUI(String name, String location, String status, String type, String reservedBy) {
        runOnUiThread(() -> {
            parkingName.setText(name != null ? name : "---");
            parkingLocation.setText(location != null ? location : "---");
            parkingType.setText(type != null ? type : "---");
            parkingStatus.setText(status != null ? status : "---");
            reserveText.setText(reservedBy);
        });
    }


    private String getStringValue(DataSnapshot snapshot, String key) {
        if (snapshot.hasChild(key)) {
            Object value = snapshot.child(key).getValue();
            return value != null ? value.toString() : null;
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void deleteParkingSlot() {
        if (currentParkingName == null) {
            Toast.makeText(this, "Error: Parking slot not found", Toast.LENGTH_SHORT).show();
            return;
        }

        parkingRef.child(currentParkingName).removeValue()
                .addOnSuccessListener(aVoid -> {
                    parkingRef.child("parkingCount").get().addOnCompleteListener(countTask -> {
                        if (countTask.isSuccessful()) {
                            Integer currentCount = countTask.getResult().getValue(Integer.class);
                            if (currentCount == null) {
                                currentCount = 1;
                            } else {
                                currentCount = currentCount - 1;
                            }
                            parkingRef.child("parkingCount").setValue(currentCount)
                                    .addOnSuccessListener(e -> {
                                        Toast.makeText(ViewParkingActivity.this,
                                                "Parking slot deleted successfully!",
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ViewParkingActivity.this,
                            "Failed to delete slot: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}