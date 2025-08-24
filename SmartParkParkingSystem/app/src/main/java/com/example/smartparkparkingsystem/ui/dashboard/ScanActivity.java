package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartparkparkingsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ScanActivity extends AppCompatActivity {

    private DatabaseReference rfidRef;
    private boolean isScanning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app");
        rfidRef = database.getReference("RFID");

        setupFirebaseListener();
    }

    private void setupFirebaseListener() {
        rfidRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get ALL data at once to ensure consistency
                Boolean scanActive = dataSnapshot.child("scanActive").getValue(Boolean.class);
                String uid = dataSnapshot.child("UID").getValue(String.class);

                // Debug: Print what we received
                System.out.println("SCAN_ACTIVE: " + scanActive);
                System.out.println("UID: " + uid);
                System.out.println("IS_SCANNING: " + isScanning);

                // Check conditions
                if (scanActive != null && scanActive == true &&
                        uid != null && !uid.isEmpty() &&
                        isScanning) {

                    // Immediately block further scans
                    isScanning = false;

                    // Go to next activity
                    Intent intent = new Intent(ScanActivity.this, EditUserActivity.class);
                    intent.putExtra("CARD_UID", uid);
                    startActivity(intent);

                    // Clear the Firebase data to prevent re-triggering
                    rfidRef.child("scanActive").setValue(false);
                    rfidRef.child("UID").setValue("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("FIREBASE ERROR: " + databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset scanning when returning to this activity
        isScanning = true;

        // Clear any old data in Firebase
        if (rfidRef != null) {
            rfidRef.child("scanActive").setValue(false);
            rfidRef.child("UID").setValue("");
        }
    }
}