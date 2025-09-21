package com.example.smartparkparkingsystem.ui.dashboard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartparkparkingsystem.R;

public class EditParkingActivity extends AppCompatActivity {

    ImageView backButton;
    Button btnDeleteParking;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editparking);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        btnDeleteParking = findViewById(R.id.btnDeleteParking);
        btnDeleteParking.setOnClickListener(v->{
            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete this parking slot?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Toast.makeText(v.getContext(), "Delete Option", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}