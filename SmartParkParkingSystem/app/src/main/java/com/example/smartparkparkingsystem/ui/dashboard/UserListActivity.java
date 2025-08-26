package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.DashboardActivity;
import com.example.smartparkparkingsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    ImageView backButton;
    ListView userListView;
    Button editUser;

    private DatabaseReference usersRef;
    private ArrayList<String> userList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        backButton = findViewById(R.id.backButton);
        userListView = findViewById(R.id.userListView);
        editUser = findViewById(R.id.editUser);

        // Firebase reference ke "Users" node
        usersRef = FirebaseDatabase
                .getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        // Setup list
        userList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        userListView.setAdapter(adapter);

        // Back button
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserListActivity.this, DashboardActivity.class);
            startActivity(intent);
        });

        // Edit User button → buka page lain
        editUser.setOnClickListener(v -> {
            Intent intent = new Intent(UserListActivity.this, EditUserActivity.class);
            startActivity(intent);
        });

        // Real-time listener
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userList.clear();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    // Make sure fields exist
                    String fullName = userSnap.child("fullName").getValue(String.class);
                    String userId = userSnap.child("userId").getValue(String.class);

                    if (fullName != null && userId != null) {
                        userList.add("UserID: " + userId + " | Name: " + fullName);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(UserListActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
