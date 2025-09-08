package com.example.smartparkparkingsystem.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private LinearLayout detailLayout;
    private TextView detailName, detailEmail;
    private Button backButtonDetail;
    private ImageView backButton;

    private ArrayList<User> userList = new ArrayList<>();
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        recyclerView = findViewById(R.id.userRecyclerView);
        detailLayout = findViewById(R.id.detailLayout);
        detailName = findViewById(R.id.detailName);
        detailEmail = findViewById(R.id.detailEmail);
        backButtonDetail = findViewById(R.id.backButtonDetail);
        backButton = findViewById(R.id.backButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(userList);
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());
        backButtonDetail.setOnClickListener(v -> showList());

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
        recyclerView.setVisibility(View.GONE);
        detailLayout.setVisibility(View.VISIBLE);
        detailName.setText(user.fullName);
        detailEmail.setText(user.email);
    }

    private void showList() {
        detailLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    // Inner User model
    private static class User {
        String id, fullName, email;

        User(String id, String fullName, String email) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
        }
    }

    // Inner RecyclerView Adapter
    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        ArrayList<User> users;

        UserAdapter(ArrayList<User> users) {
            this.users = users;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = users.get(position);
            holder.name.setText(user.fullName);
            holder.email.setText(user.email);
            holder.itemView.setOnClickListener(v -> showUserDetail(user));
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        class UserViewHolder extends RecyclerView.ViewHolder {
            TextView name, email;

            UserViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(android.R.id.text1);
                email = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}
