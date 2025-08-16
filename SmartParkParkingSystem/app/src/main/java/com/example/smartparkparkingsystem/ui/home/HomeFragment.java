package com.example.smartparkparkingsystem.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartparkparkingsystem.MainActivity;
import com.example.smartparkparkingsystem.R;
import com.example.smartparkparkingsystem.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {
// File for Homepage (FRAGMENT!!)
    // Keep in mind, fragment work differently dari Activity.
    // Apa2, google or chatgpt je kalau confuse dengan difference dia
    private TextView welcomeText, infoText;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        welcomeText = view.findViewById(R.id.welcomeText);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();



        String userId = mAuth.getCurrentUser().getUid();

        // Ambil nama pengguna dari database
        mDatabase.child("users").child(userId).child("fullName").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String fullName = task.getResult().getValue(String.class);
                welcomeText.setText("Welcome, " + fullName + "!");
            } else {
                welcomeText.setText("Welcome!");
            }
        });

        return view;
    }
}