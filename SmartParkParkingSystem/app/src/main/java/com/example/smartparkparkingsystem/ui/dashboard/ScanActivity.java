package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartparkparkingsystem.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ScanActivity extends AppCompatActivity {

    private DatabaseReference rfidRef;
    private boolean isScanning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app");
        rfidRef = database.getReference("RFID");
        rfidRef.child("registerMode").setValue(true);


        setupFirebaseListener();
    }

    private void setupFirebaseListener() {
        rfidRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Boolean scanActive = dataSnapshot.child("scanActive").getValue(Boolean.class);
                String uid = dataSnapshot.child("UID").getValue(String.class);

                System.out.println("SCAN_ACTIVE: " + scanActive);
                System.out.println("UID: " + uid);
                System.out.println("IS_SCANNING: " + isScanning);

                if (scanActive != null && scanActive == true &&
                        uid != null && !uid.isEmpty() &&
                        isScanning) {

                    isScanning = false;

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("CARD_UID", uid);
                    setResult(RESULT_OK, resultIntent);
                    finish();

                    rfidRef.child("registerMode").setValue(false);
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
        isScanning = true;

        if (rfidRef != null) {
            rfidRef.child("scanActive").setValue(false);
            rfidRef.child("UID").setValue("");
        }
    }


}