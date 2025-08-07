package com.example.smartparkparkingsystem.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.smartparkparkingsystem.MainActivity;
import com.example.smartparkparkingsystem.R;

public class ProfileFragment extends Fragment {
    // File for Profile (FRAGMENT!!)
    // Keep in mind, fragment work differently dari Activity.
    // Apa2, google or chatgpt je kalau confuse dengan difference dia

    Button editBtn; // Testing edit button
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Edit button opens Edit Profile Activity. Intent dia tu boleh gunakan.
        editBtn = view.findViewById(R.id.editBtn);
        editBtn.setOnClickListener(v-> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        return view;
    }
}