package com.example.smartparkparkingsystem.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.smartparkparkingsystem.R;
import com.example.smartparkparkingsystem.ReserveActivity;
import com.example.smartparkparkingsystem.StatusActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView welcomeText, dateTimeText, parkingAvailability;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    LinearLayout findParking, bookParking;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        welcomeText = view.findViewById(R.id.welcomeText);
        dateTimeText = view.findViewById(R.id.dateTimeText);
        findParking = view.findViewById(R.id.findParking);
        bookParking = view.findViewById(R.id.bookParking);

        findParking.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), StatusActivity.class);
            startActivity(intent);
        });

        bookParking.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), ReserveActivity.class);
            startActivity(intent);
        });


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase
                .getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();


        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            mDatabase.child("users").child(userId).child("fullName")
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().getValue() != null) {
                            String fullName = task.getResult().getValue(String.class);
                            welcomeText.setText("Hi, " + fullName + "!");
                        } else {
                            welcomeText.setText("Hi, User!");
                        }
                    });
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        dateTimeText.setText(currentDateTime);

        /** ✅ Live updates for parking availability
        mDatabase.child("slots").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long total = snapshot.child("total").getValue(Long.class);
                Long occupied = snapshot.child("occupied").getValue(Long.class);

                if (total != null && occupied != null) {
                    long available = total - occupied;
                    parkingAvailability.setText("Available Slots: " + available + "/" + total);
                } else {
                    parkingAvailability.setText("Available Slots: Data missing");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                parkingAvailability.setText("Available Slots: Error");
            }
        });**/

        return view;
    }
}
