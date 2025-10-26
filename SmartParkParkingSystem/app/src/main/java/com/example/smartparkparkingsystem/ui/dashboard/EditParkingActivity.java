package com.example.smartparkparkingsystem.ui.dashboard;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

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

        initViews();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/");
        parkingRef = database.getReference("Parking");

        editSlotName.setFilters(new InputFilter[]{
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(3)
        });

        editPlateNumber.setFilters(new InputFilter[]{
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(10)
        });

        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(EditParkingActivity.this, editLocation);
                popupMenu.getMenuInflater().inflate(R.menu.locationmenu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        editLocation.setText(item.getTitle());
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

        editType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(EditParkingActivity.this, editType);
                popupMenu.getMenuInflater().inflate(R.menu.typemenu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        editType.setText(item.getTitle());
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

        editStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(EditParkingActivity.this, editStatus);
                popupMenu.getMenuInflater().inflate(R.menu.statusmenu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        editStatus.setText(item.getTitle());
                        return true;
                    }
                });

                popupMenu.show();
            }
        });



        getIntentData();

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

        originalSlotName = getIntent().getStringExtra("parkingName");
        String location = getIntent().getStringExtra("parkingLocation");
        String type = getIntent().getStringExtra("parkingType");
        String status = getIntent().getStringExtra("parkingStatus");
        String reservedBy = getIntent().getStringExtra("reservedBy");

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

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Delete Parking Slot")
                    .setMessage("Are you sure you want to delete this parking slot?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteParkingSlot();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void updateParkingSlot() {

        String newSlotName = editSlotName.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String type = editType.getText().toString().trim();
        String status = editStatus.getText().toString().trim();
        String plateNumber = editPlateNumber.getText().toString().trim();

        if (newSlotName.isEmpty()) {
            editSlotName.setError("Please enter slot name.");
            editSlotName.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            editLocation.setError("Please select location.");
            editLocation.requestFocus();
            return;
        }

        if (type.isEmpty()) {
            editType.setError("Please select parking type.");
            editType.requestFocus();
            return;
        }

        if (status.isEmpty()) {
            editStatus.setError("Please select status.");
            editStatus.requestFocus();
            return;
        }

        Parking parking = new Parking();
        parking.setName(newSlotName);
        parking.setLocation(location);
        parking.setType(type);
        parking.setStatus(status);
        parking.setReservedby("Reserved".equalsIgnoreCase(status) ? plateNumber : "");

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

            parkingRef.child(newSlotName).setValue(parking)
                    .addOnSuccessListener(aVoid -> {
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
                                        Toast.makeText(EditParkingActivity.this,
                                                "Parking slot deleted successfully!",
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditParkingActivity.this,
                            "Failed to delete slot: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }}