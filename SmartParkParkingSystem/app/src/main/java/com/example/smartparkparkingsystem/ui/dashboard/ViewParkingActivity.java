package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewParkingActivity extends AppCompatActivity {

    private ImageView backButton;
    private Button editBtn;
    private ImageButton delBtn;

    private TextView parkingName, parkingStatus, parkingLocation, parkingType, reserveText;

    private DatabaseReference parkingRef;

    private String slotKey; // key of selected parking slot

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
        parkingRef = FirebaseDatabase.getInstance().getReference("Parking");

        // Get clicked parking details from Intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("Name");
        String status = intent.getStringExtra("Status");
        String location = intent.getStringExtra("Location");
        String type = intent.getStringExtra("parkingtype");
        String reservedBy = intent.getStringExtra("Reservedby");

        // Set values
        parkingName.setText(name != null ? name : "---");
        parkingStatus.setText(status != null ? status : "---");
        parkingLocation.setText(location != null ? location : "---");
        parkingType.setText(type != null ? type : "---");
        reserveText.setText(reservedBy != null ? reservedBy : "---");

        backButton.setOnClickListener(v -> finish());

        editBtn.setOnClickListener(v -> {
            Intent editIntent = new Intent(ViewParkingActivity.this, EditParkingActivity.class);
            editIntent.putExtra("Name", name);
            startActivity(editIntent);
        });

        delBtn.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Parking Slot")
                    .setMessage("Are you sure you want to delete this parking slot?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (name != null) {
                            parkingRef.child(name).removeValue();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}
0