package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewParkingActivity extends AppCompatActivity {

    ImageView backButton;
    Button editBtn;
    ImageButton delBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_parking);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        editBtn = findViewById(R.id.editBtn);
        delBtn = findViewById(R.id.delBtn);

        editBtn.setOnClickListener(v->{
            Intent intent = new Intent(ViewParkingActivity.this, EditParkingActivity.class);
            startActivity(intent);
        });

        delBtn.setOnClickListener(v->{
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
