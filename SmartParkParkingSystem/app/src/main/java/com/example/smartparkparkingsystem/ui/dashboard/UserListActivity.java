package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartparkparkingsystem.R;

public class UserListActivity extends AppCompatActivity {

    Button editUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        editUser = findViewById(R.id.editUser);

        editUser.setOnClickListener(v -> {
                Intent intent = new Intent(UserListActivity.this, EditUserActivity.class);
                startActivity(intent);
        });
    }
}