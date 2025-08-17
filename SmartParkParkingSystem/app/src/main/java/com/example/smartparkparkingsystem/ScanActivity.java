package com.example.smartparkparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ScanActivity extends AppCompatActivity {

    // will change later when dashboard dah set up
    private DatabaseReference rfidRef;
    private boolean isScanning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        rfidRef = database.getReference("RFID");

        setupFirebaseListener();
    }

    private void setupFirebaseListener() {
        rfidRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean scanActive = dataSnapshot.child("scanActive").getValue(Boolean.class);
                String uid = dataSnapshot.child("UID").getValue(String.class);

                if (scanActive != null && scanActive
                        && uid != null && !uid.isEmpty()
                        && isScanning) {

                    isScanning = false;

                    Intent intent = new Intent(ScanActivity.this, EditUserActivity.class);
                    intent.putExtra("CARD_UID", uid);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error", databaseError.toException());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isScanning = true;
    }
}