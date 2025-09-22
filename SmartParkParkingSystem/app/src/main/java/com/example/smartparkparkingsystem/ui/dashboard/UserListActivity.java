package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView backButton;
    private ArrayList<User> userList = new ArrayList<>();
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        recyclerView = findViewById(R.id.userRecyclerView);
        backButton = findViewById(R.id.backButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(userList);
        recyclerView.setAdapter(adapter);
        backButton.setOnClickListener(v -> finish());

        loadUsers();
    }

    private void loadUsers() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String id = userSnapshot.getKey();
                    String fullName = userSnapshot.child("fullName").getValue(String.class);
                    String email = userSnapshot.child("email").getValue(String.class);
                    if (fullName != null && email != null) {
                        userList.add(new User(id, fullName, email));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserListActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserDetail(User user) {
        Intent intent = new Intent(this, ViewUserActivity.class);
        intent.putExtra("userId", user.id);
        startActivity(intent);
    }

    // User model
    private static class User {
        String id, fullName, email;

        User(String id, String fullName, String email) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
        }
    }

    // RecyclerView Adapter
    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        ArrayList<User> users;

        UserAdapter(ArrayList<User> users) {
            this.users = users;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = users.get(position);
            holder.name.setText(user.fullName);
            holder.email.setText(user.email);

            // Open detail view
            holder.itemView.setOnClickListener(v -> showUserDetail(user));

            // Edit button -> open EditUserActivity
            holder.btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), EditUserActivity.class);
                intent.putExtra("userId", user.id);
                v.getContext().startActivity(intent);
            });

            // Delete button -> confirm then delete
            holder.btnDelete.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete " + user.fullName + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                            usersRef.child(user.id).removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(v.getContext(), "User deleted", Toast.LENGTH_SHORT).show();
                                        users.remove(position);
                                        notifyItemRemoved(position);
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(v.getContext(), "Failed to delete user", Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        class UserViewHolder extends RecyclerView.ViewHolder {
            TextView name, email;
            ImageButton btnEdit, btnDelete;

            UserViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.textUserName);
                email = itemView.findViewById(R.id.textUserEmail);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}
