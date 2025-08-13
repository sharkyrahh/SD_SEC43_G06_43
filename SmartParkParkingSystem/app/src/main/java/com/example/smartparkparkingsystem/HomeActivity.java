package com.example.smartparkparkingsystem;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeActivity extends Fragment {

    public HomeActivity() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Setup UI elements
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView welcomeText = view.findViewById(R.id.welcomeText);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView infoText = view.findViewById(R.id.infoText);

        welcomeText.setText("Welcome, Hazim!");
        infoText.setText("This is your SmartPark dashboard. Tap the menu to get started.");

        return view;
    }
}