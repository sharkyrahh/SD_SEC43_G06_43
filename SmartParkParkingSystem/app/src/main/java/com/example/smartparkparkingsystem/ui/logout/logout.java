package com.example.smartparkparkingsystem.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartparkparkingsystem.MainActivity;
import com.example.smartparkparkingsystem.R;
import com.google.firebase.auth.FirebaseAuth;

public class logout extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Use your existing fragment_logout.xml
        return inflater.inflate(R.layout.nav_header_home, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        // 1) Sign out
        FirebaseAuth.getInstance().signOut();

        // 2) Toast
        Toast.makeText(requireContext(), "Logout successful", Toast.LENGTH_SHORT).show();

        // 3) Go to Login (MainActivity) and clear back stack
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // 4) Finish the hosting activity so back can't return here
        requireActivity().finish();
    }
}
