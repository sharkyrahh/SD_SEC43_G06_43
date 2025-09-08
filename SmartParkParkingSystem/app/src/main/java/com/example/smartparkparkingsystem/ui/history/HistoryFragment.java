package com.example.smartparkparkingsystem.ui.history;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.smartparkparkingsystem.R;
import com.google.android.material.tabs.TabLayout;

public class HistoryFragment extends Fragment {

    private ViewPager viewPager; // Add this line

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        viewPager = view.findViewById(R.id.pager); // Make sure this ID exists in your XML
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);

        // Setup ViewPager adapter
        DemoCollectionPagerAdapter adapter = new DemoCollectionPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    // Move the adapter class inside HistoryFragment but as a nested class
    public static class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {

        public DemoCollectionPagerAdapter(@NonNull androidx.fragment.app.FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            // You'll need to create these fragment classes
            // For now, return null or create simple fragments
            return new Fragment(); // Replace with your actual fragments
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }
}