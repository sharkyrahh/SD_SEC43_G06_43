package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView parkingName, parkingStatus, parkingLocation, parkingType, reserveText;

    private DatabaseReference parkingRef;
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

        // Firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/");
        parkingRef = database.getReference("Parking");

        // Get parking name from Intent
        Intent intent = getIntent();
        currentParkingName = intent.getStringExtra("parkingName");

        if (currentParkingName != null) {
            // Load data from Firebase with real-time listener
            loadParkingDataFromFirebase(currentParkingName);
        } else {
            Toast.makeText(this, "Error: No parking slot specified", Toast.LENGTH_SHORT).show();
            finish();
        }

        backButton.setOnClickListener(v -> finish());

        editBtn.setOnClickListener(v -> {
            // Pass current data to EditParkingActivity
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
                            parkingRef.child(currentParkingName).removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(ViewParkingActivity.this,
                                                "Parking slot deleted successfully!",
                                                Toast.LENGTH_SHORT).show();
                                        finish(); // Close activity after deletion
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ViewParkingActivity.this,
                                                "Failed to delete parking slot: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    });
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
                    // Get data from Firebase
                    String name = parkingName;
                    String location = getStringValue(snapshot, "location");
                    String status = getStringValue(snapshot, "status");
                    String type = getStringValue(snapshot, "type");
                    String reservedBy = getStringValue(snapshot, "reservedby");

                    // Update UI with latest data
                    updateUI(name, location, status, type, reservedBy);
                } else {
                    // Parking slot no longer exists
                    Toast.makeText(ViewParkingActivity.this,
                            "Parking slot no longer exists",
                            Toast.LENGTH_SHORT).show();
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

    private void updateUI(String name, String location, String status, String type, String reservedBy) {
        // Run on UI thread to update views
        runOnUiThread(() -> {
            parkingName.setText(name != null ? name : "---");
            parkingLocation.setText(location != null ? location : "---");
            parkingType.setText(type != null ? type : "---");
            parkingStatus.setText(status != null ? status : "---");

            // Set reserved text with proper formatting
            if (reservedBy != null && !reservedBy.isEmpty()) {
                reserveText.setText(reservedBy);
            } else {
                reserveText.setText("Not Reserved");
            }

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
        // Remove listeners if needed (Firebase automatically handles this for addValueEventListener)
    }
}