package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        rfidRef = database.getReference("RFID");

        setupFirebaseListener();
    }

    private void setupFirebaseListener() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean scanActive = dataSnapshot.child("scanActive").getValue(Boolean.class);
                String uid = dataSnapshot.child("UID").getValue(String.class);

                if (scanActive != null && scanActive
                        && uid != null && !uid.isEmpty()
                        && isScanning) {

                    isScanning = false;

                    // Remove listener temporarily to prevent multiple triggers
                    rfidRef.removeEventListener(valueEventListener);

                    Intent intent = new Intent(ScanActivity.this, EditUserActivity.class);
                    intent.putExtra("CARD_UID", uid);
                    startActivity(intent);

                    // Optional: finish this activity
                    // finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error", databaseError.toException());
            }
        };

        rfidRef.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isScanning = true;

        // Re-add listener when returning to this activity
        if (valueEventListener != null) {
            rfidRef.addValueEventListener(valueEventListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove listener when activity is paused
        if (valueEventListener != null) {
            rfidRef.removeEventListener(valueEventListener);
        }
    }
}