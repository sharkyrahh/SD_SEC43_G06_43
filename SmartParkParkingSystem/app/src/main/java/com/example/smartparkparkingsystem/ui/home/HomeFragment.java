package com.example.smartparkparkingsystem.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartparkparkingsystem.MainActivity;
import com.example.smartparkparkingsystem.R;
import com.example.smartparkparkingsystem.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
// File for Homepage (FRAGMENT!!)
    // Keep in mind, fragment work differently dari Activity.
    // Apa2, google or chatgpt je kalau confuse dengan difference dia

    Button logoutBtn; // Testing logout button
    private FragmentHomeBinding binding;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Testing logout button. Just moves to the Sign Up activity page. Nanti tukarla kasi elok sikit,
        // letak dekat navigation bar ke somewhere lagi cantik ke
        logoutBtn = view.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v-> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });

        return view;
    }
}