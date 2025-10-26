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

public class AddParkingActivity extends AppCompatActivity {

    private EditText addSlotName, addLocation, addType, addStatus, addPlateNumber;
    private Button btnAddParking;
    private ImageView backButton;

    private DatabaseReference parkingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addparking);


        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/");
        parkingRef = database.getReference("Parking");

        initViews();

        setupClickListeners();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        addSlotName = findViewById(R.id.addSlotName);
        addLocation = findViewById(R.id.addLocation);
        addType = findViewById(R.id.addType);
        addStatus = findViewById(R.id.addStatus);
        addPlateNumber = findViewById(R.id.addPlateNumber);
        btnAddParking = findViewById(R.id.btnAddParking);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        btnAddParking.setOnClickListener(v -> addParkingSlot());
    }

    private void addParkingSlot() {
        String slotName = addSlotName.getText().toString().trim();
        String location = addLocation.getText().toString().trim();
        String type = addType.getText().toString().trim();
        String status = addStatus.getText().toString().trim();
        String plateNumber = addPlateNumber.getText().toString().trim();

        if (slotName.isEmpty()) {
            addSlotName.setError("Slot name is required");
            addSlotName.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            addLocation.setError("Location is required");
            addLocation.requestFocus();
            return;
        }

        if (type.isEmpty()) {
            addType.setError("Parking type is required");
            addType.requestFocus();
            return;
        }

        if (status.isEmpty()) {
            addStatus.setError("Status is required");
            addStatus.requestFocus();
            return;
        }


        if (!isValidStatus(status)) {
            addStatus.setError("Status must be: Available, Full, or Reserved");
            addStatus.requestFocus();
            return;
        }


        Parking parking = new Parking();
        parking.setName(slotName);
        parking.setLocation(location);
        parking.setType(type);
        parking.setStatus(status);


        if ("Reserved".equalsIgnoreCase(status) && !plateNumber.isEmpty()) {
            parking.setReservedby(plateNumber);
        } else {
            parking.setReservedby("");
        }

        saveParkingToFirebase(slotName, parking);
    }

    private boolean isValidStatus(String status) {
        String statusLower = status.toLowerCase();
        return "available".equals(statusLower) ||
                "full".equals(statusLower) ||
                "reserved".equals(statusLower);
    }

    private void saveParkingToFirebase(String slotName, Parking parking) {

        parkingRef.child(slotName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {

                    Toast.makeText(AddParkingActivity.this,
                            "Parking slot " + slotName + " already exists!",
                            Toast.LENGTH_LONG).show();
                } else {

                    parkingRef.child(slotName).setValue(parking)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AddParkingActivity.this,
                                        "Parking slot added successfully!",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AddParkingActivity.this,
                                        "Failed to add parking slot: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                }
            } else {
                Toast.makeText(AddParkingActivity.this,
                        "Error checking parking slot: " + task.getException().getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}