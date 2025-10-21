package com.example.smartparkparkingsystem.ui.dashboard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditParkingActivity extends AppCompatActivity {

    private ImageView backButton;
    private EditText editSlotName, editLocation, editType, editStatus, editPlateNumber;
    private Button btnSaveParking, btnDeleteParking;

    private DatabaseReference parkingRef;
    private String originalSlotName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editparking);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/");
        parkingRef = database.getReference("Parking");

        // Initialize views
        initViews();

        // Get data from intent
        getIntentData();

        // Set up click listeners
        setupClickListeners();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        editSlotName = findViewById(R.id.editSlotName);
        editLocation = findViewById(R.id.editLocation);
        editType = findViewById(R.id.editType);
        editStatus = findViewById(R.id.editStatus);
        editPlateNumber = findViewById(R.id.editPlateNumber);
        btnSaveParking = findViewById(R.id.btnSaveParking);
        btnDeleteParking = findViewById(R.id.btnDeleteParking);
    }

    private void getIntentData() {
        // Get parking data from intent
        originalSlotName = getIntent().getStringExtra("parkingName");
        String location = getIntent().getStringExtra("parkingLocation");
        String type = getIntent().getStringExtra("parkingType");
        String status = getIntent().getStringExtra("parkingStatus");
        String reservedBy = getIntent().getStringExtra("reservedBy");

        // Set data to EditText fields
        if (originalSlotName != null) {
            editSlotName.setText(originalSlotName);
        }
        if (location != null) {
            editLocation.setText(location);
        }
        if (type != null) {
            editType.setText(type);
        }
        if (status != null) {
            editStatus.setText(status);
        }
        if (reservedBy != null && !reservedBy.isEmpty()) {
            editPlateNumber.setText(reservedBy);
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        btnSaveParking.setOnClickListener(v -> updateParkingSlot());

        btnDeleteParking.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Parking Slot")
                    .setMessage("Are you sure you want to delete this parking slot?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteParkingSlot())
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void updateParkingSlot() {
        // Get updated values
        String newSlotName = editSlotName.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String type = editType.getText().toString().trim();
        String status = editStatus.getText().toString().trim();
        String plateNumber = editPlateNumber.getText().toString().trim();

        // Validate required fields
        if (newSlotName.isEmpty()) {
            editSlotName.setError("Slot name is required");
            editSlotName.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            editLocation.setError("Location is required");
            editLocation.requestFocus();
            return;
        }

        if (type.isEmpty()) {
            editType.setError("Parking type is required");
            editType.requestFocus();
            return;
        }

        if (status.isEmpty()) {
            editStatus.setError("Status is required");
            editStatus.requestFocus();
            return;
        }

        // Validate status value
        if (!isValidStatus(status)) {
            editStatus.setError("Status must be: Available, Full, or Reserved");
            editStatus.requestFocus();
            return;
        }

        // Create updated Parking object
        Parking parking = new Parking();
        parking.setName(newSlotName);
        parking.setLocation(location);
        parking.setType(type);
        parking.setStatus(status);
        parking.setReservedby("Reserved".equalsIgnoreCase(status) ? plateNumber : "");

        // Update in Firebase
        updateParkingInFirebase(newSlotName, parking);
    }

    private boolean isValidStatus(String status) {
        String statusLower = status.toLowerCase();
        return "available".equals(statusLower) ||
                "full".equals(statusLower) ||
                "reserved".equals(statusLower);
    }

    private void updateParkingInFirebase(String newSlotName, Parking parking) {
        if (originalSlotName == null) {
            Toast.makeText(this, "Error: Original parking slot not found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (originalSlotName.equals(newSlotName)) {
            // Slot name didn't change, just update the data
            parkingRef.child(originalSlotName).setValue(parking)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditParkingActivity.this,
                                "Parking slot updated successfully!",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditParkingActivity.this,
                                "Failed to update parking slot: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        } else {
            // Slot name changed, need to delete old and create new
            parkingRef.child(newSlotName).setValue(parking)
                    .addOnSuccessListener(aVoid -> {
                        // Delete the old slot
                        parkingRef.child(originalSlotName).removeValue()
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(EditParkingActivity.this,
                                            "Parking slot updated successfully!",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(EditParkingActivity.this,
                                            "Error removing old slot: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditParkingActivity.this,
                                "Failed to update parking slot: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        }
    }

    private void deleteParkingSlot() {
        if (originalSlotName == null) {
            Toast.makeText(this, "Error: Parking slot not found", Toast.LENGTH_SHORT).show();
            return;
        }

        parkingRef.child(originalSlotName).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditParkingActivity.this,
                            "Parking slot deleted successfully!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditParkingActivity.this,
                            "Failed to delete parking slot: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}