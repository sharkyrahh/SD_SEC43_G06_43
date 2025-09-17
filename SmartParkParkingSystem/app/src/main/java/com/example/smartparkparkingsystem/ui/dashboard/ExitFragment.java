package com.example.smartparkparkingsystem.ui.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.smartparkparkingsystem.R;
import com.example.smartparkparkingsystem.ui.dashboard.EntryAdapterAdmin;

import java.util.ArrayList;
import java.util.List;


public class ExitFragment extends Fragment {

    EntryAdapterAdmin adapter;

    private List<String> userList;
    private List<String> dateList;
    private List<String> timeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exitadmin, container, false);

        // Create separate dummy data for dates and times
        dateList = new ArrayList<>();
        timeList = new ArrayList<>();
        userList = new ArrayList<>();

        // Add dummy data
        dateList.add("01-12-2025"); timeList.add("13:25PM"); userList.add("Kira");
        dateList.add("01-15-2025"); timeList.add("08:30AM"); userList.add("Kira");
        dateList.add("01-15-2025"); timeList.add("12:15PM"); userList.add("Kira");
        dateList.add("01-14-2025"); timeList.add("09:45AM"); userList.add("Kira");
        dateList.add("01-14-2025"); timeList.add("14:20PM"); userList.add("Kira");
        dateList.add("01-13-2025"); timeList.add("10:05AM"); userList.add("Kira");
        dateList.add("01-13-2025"); timeList.add("16:40PM"); userList.add("Kira");
        dateList.add("01-12-2025"); timeList.add("07:55AM"); userList.add("Kira");


        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.entryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EntryAdapterAdmin(getContext(), dateList, timeList, userList);
        adapter.setClickListener((EntryAdapterAdmin.ItemClickListener) this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void onItemClick(View view, int position) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Entry on " + dateList.get(position) + " at " + timeList.get(position), Toast.LENGTH_SHORT).show();
        }
    }
}