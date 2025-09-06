package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    RecyclerView userRecyclerView;
    Button editUser;

    private DatabaseReference usersRef;
    private ArrayList<User> userList;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        backButton = findViewById(R.id.backButton);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        editUser = findViewById(R.id.editUser);

        // Firebase reference ke "Users" node
        usersRef = FirebaseDatabase
                .getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        // Setup RecyclerView
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, new UserAdapter.OnUserActionListener() {
            @Override
            public void onEdit(User user) {
                Intent intent = new Intent(UserListActivity.this, EditUserActivity.class);
                intent.putExtra("userId", user.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(User user) {
                usersRef.child(user.getId()).removeValue();
            }
        });
        userRecyclerView.setAdapter(userAdapter);

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
                    String id = userSnap.getKey(); // Key = ID user
                    String fullname = userSnap.child("fullname").getValue(String.class);
                    String email = userSnap.child("email").getValue(String.class);

                    if (fullname != null && email != null) {
                        userList.add(new User(id, fullname, email));
                    }
                }

                userAdapter.notifyDataSetChanged(); // refresh RecyclerView
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(UserListActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
