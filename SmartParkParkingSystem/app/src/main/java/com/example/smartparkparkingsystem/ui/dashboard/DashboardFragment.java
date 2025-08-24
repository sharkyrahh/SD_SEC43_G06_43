package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartparkparkingsystem.MainActivity;
import com.example.smartparkparkingsystem.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

import android.widget.Toast;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        MaterialCardView cardViewUser = view.findViewById(R.id.card_view_user);
        MaterialCardView cardViewPortfolio = view.findViewById(R.id.card_view_portfolio);
        MaterialCardView cardEditProfile = view.findViewById(R.id.card_edit_profile);
        MaterialCardView cardLogout = view.findViewById(R.id.card_logout);

        // Setup Toolbar (if needed)
      

        // Click Listeners
        cardViewUser.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View User clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), UserListActivity.class));
        });

        cardViewPortfolio.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View Portfolio clicked", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(getActivity(), PortfolioActivity.class));
        });

        cardEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(getActivity(), EditProfileActivity.class));
        });

        // Logout Button
        cardLogout.setOnClickListener(v -> {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Toast message
            Toast.makeText(getContext(), "Logout successful", Toast.LENGTH_SHORT).show();

            // Redirect to MainActivity (login screen)
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // Close current activity so user cannot go back
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }
}