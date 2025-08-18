package com.example.smartparkparkingsystem.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartparkparkingsystem.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import android.widget.Toast;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Bind Views
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        MaterialCardView cardViewUser = view.findViewById(R.id.card_view_user);
        MaterialCardView cardViewPortfolio = view.findViewById(R.id.card_view_portfolio);
        MaterialCardView cardEditProfile = view.findViewById(R.id.card_edit_profile);
        MaterialCardView cardLogout = view.findViewById(R.id.card_logout);

        // Setup Toolbar (if needed)
        toolbar.setNavigationOnClickListener(v ->
                Toast.makeText(getContext(), "Toolbar navigation clicked", Toast.LENGTH_SHORT).show()
        );

        // Click Listeners
        cardViewUser.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View User clicked", Toast.LENGTH_SHORT).show();
            // Example: open UserActivity
            // startActivity(new Intent(getActivity(), UserActivity.class));
        });

        cardViewPortfolio.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View Portfolio clicked", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(getActivity(), PortfolioActivity.class));
        });

        cardEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(getActivity(), EditProfileActivity.class));
        });

        cardLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
            // Add logout logic here (e.g., clear session, go back to LoginActivity)
        });

        return view;
    }
}
