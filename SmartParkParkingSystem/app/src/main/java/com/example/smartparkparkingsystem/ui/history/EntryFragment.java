package com.example.smartparkparkingsystem.ui.history;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EntryFragment extends Fragment {

    EntryAdapter adapter;
    private ArrayList<EntryLog> entryList = new ArrayList<>(); // Use local EntryLog

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.entryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EntryAdapter(getContext(), entryList);
        recyclerView.setAdapter(adapter);

        loadEntry();
        return view;
    }

    private void loadEntry() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        DatabaseReference entryRef = FirebaseDatabase.getInstance().getReference("entryLog");
        entryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<EntryLog> tempList = new ArrayList<>();

                for (DataSnapshot randomChildSnapshot : snapshot.getChildren()) {
                    String entryUid = randomChildSnapshot.child("UID").getValue(String.class);


                    if (uid.equals(entryUid)) {
                        String timestamp = randomChildSnapshot.child("timestamp").getValue(String.class);
                        String day = randomChildSnapshot.child("day").getValue(String.class);
                        String date = randomChildSnapshot.child("date").getValue(String.class);

                        if (timestamp != null && day != null && date != null) {
                            tempList.add(new EntryLog(timestamp, day, date));
                        }
                    }
                }

                entryList.clear();
                for (int i = tempList.size() - 1; i >= 0; i--) {
                    entryList.add(tempList.get(i));
                }

                adapter.notifyDataSetChanged();

                if (entryList.isEmpty()) {
                    Toast.makeText(getContext(), "No entry logs found for your account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load entry logs: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onItemClick(View view, int position) {
        if (getContext() != null) {
        }
    }

    public class EntryLog {
        String time;
        String day;
        String date;

        public EntryLog(String time, String day, String date) {
            this.time = time;
            this.day = day;
            this.date = date;
        }
    }

    private class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {
        ArrayList<EntryLog> entryLogs;

        EntryAdapter(Context context, ArrayList<EntryLog> entryLogs) {
            this.entryLogs = entryLogs;
        }

        @NonNull
        @Override
        public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.log, parent, false);
            return new EntryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
            EntryLog entryLog = entryLogs.get(position);
            holder.time.setText(entryLog.time);
            holder.day.setText(entryLog.day);
            holder.date.setText(entryLog.date);
        }

        @Override
        public int getItemCount() {
            return entryLogs.size();
        }

        class EntryViewHolder extends RecyclerView.ViewHolder {
            TextView time, day, date;

            EntryViewHolder(@NonNull View itemView) {
                super(itemView);
                time = itemView.findViewById(R.id.textTime);
                day = itemView.findViewById(R.id.textDay);
                date = itemView.findViewById(R.id.textDate);
            }
        }
    }
}