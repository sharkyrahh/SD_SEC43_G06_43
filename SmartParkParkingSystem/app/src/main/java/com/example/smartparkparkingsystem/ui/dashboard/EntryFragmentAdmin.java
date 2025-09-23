package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EntryFragmentAdmin extends Fragment {

    EntryAdapterAdmin adapter;
    private ArrayList<EntryLog> entryList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entryadmin, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.entryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EntryAdapterAdmin(getContext(), entryList);
        recyclerView.setAdapter(adapter);

        loadEntry();
        return view;
    }

    private void loadEntry() {
        DatabaseReference entryRef = FirebaseDatabase.getInstance().getReference("entryLog");
        entryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entryList.clear();
                List<EntryFragmentAdmin.EntryLog> tempList = new ArrayList<>();

                for (DataSnapshot randomChildSnapshot : snapshot.getChildren()) {
                    String timestamp = randomChildSnapshot.child("timestamp").getValue(String.class);

                    if (timestamp != null) {
                        // Single level data
                        String day = randomChildSnapshot.child("day").getValue(String.class);
                        String date = randomChildSnapshot.child("date").getValue(String.class);
                        String plate = randomChildSnapshot.child("plateNum").getValue(String.class);

                        if (day != null && date != null && plate != null) {
                            tempList.add(new EntryFragmentAdmin.EntryLog(timestamp, day, date, plate));
                        }
                    } else {
                        // Nested data
                        for (DataSnapshot entrySnapshot : randomChildSnapshot.getChildren()) {
                            timestamp = entrySnapshot.child("timestamp").getValue(String.class);
                            String day = entrySnapshot.child("day").getValue(String.class);
                            String date = entrySnapshot.child("date").getValue(String.class);
                            String plate = entrySnapshot.child("plateNum").getValue(String.class);

                            if (timestamp != null && day != null && date != null && plate != null) {
                                tempList.add(new EntryFragmentAdmin.EntryLog(timestamp, day, date, plate));
                            }
                        }
                    }
                }

                // Reverse the list for most recent first
                for (int i = tempList.size() - 1; i >= 0; i--) {
                    entryList.add(tempList.get(i));
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    public void onItemClick(View view, int position) {
        if (getContext() != null) {
            //Toast.makeText(getContext(), "Entry on " + dateList.get(position) + " at " + timeList.get(position), Toast.LENGTH_SHORT).show();
        }
    }

    public static class EntryLog {
        public String time;
        public String day;
        public String date;
        String plate;

        public EntryLog(String time, String day, String date, String plate) {
            this.time = time;
            this.day = day;
            this.date = date;
            this.plate = plate;
        }
    }

    private class EntryAdapterAdmin extends RecyclerView.Adapter<EntryFragmentAdmin.EntryAdapterAdmin.EntryViewHolder> {
        ArrayList<EntryFragmentAdmin.EntryLog> entryLogs;

        EntryAdapterAdmin(Context context, ArrayList<EntryLog> entryLogs) {
            this.entryLogs = entryLogs;
        }

        @NonNull
        @Override
        public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.logadmin, parent, false);
            return new EntryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
            EntryLog entryLog = entryLogs.get(position);
            holder.time.setText(entryLog.time);
            holder.day.setText(entryLog.day);
            holder.date.setText(entryLog.date);
            holder.plate.setText(entryLog.plate);

            // Open detail view
            //holder.itemView.setOnClickListener(v -> showUserDetail(user));

        }

        @Override
        public int getItemCount() {
            return entryLogs.size();
        }

        class EntryViewHolder extends RecyclerView.ViewHolder {
            TextView time, day, date, plate;

            EntryViewHolder(@NonNull View itemView) {
                super(itemView);
                time = itemView.findViewById(R.id.textTime);
                day = itemView.findViewById(R.id.textDay);
                date = itemView.findViewById(R.id.textDate);
                plate = itemView.findViewById(R.id.textPlate);
            }
        }
    }
}