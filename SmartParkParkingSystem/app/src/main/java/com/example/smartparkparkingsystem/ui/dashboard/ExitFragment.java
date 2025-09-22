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

public class ExitFragment extends Fragment {

    ExitAdapterAdmin adapter;
    private ArrayList<ExitLog> exitList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exitadmin, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.exitList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExitAdapterAdmin(getContext(), exitList);
        recyclerView.setAdapter(adapter);

        loadExit();
        return view;
    }

    private void loadExit() {
        DatabaseReference exitRef = FirebaseDatabase.getInstance().getReference("exitLog");
        exitRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                exitList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String timestamp = userSnapshot.child("timestamp").getValue(String.class);
                    String day = userSnapshot.child("day").getValue(String.class);
                    String date = userSnapshot.child("date").getValue(String.class);
                    String plate = userSnapshot.child("plateNum").getValue(String.class);
                    if (timestamp != null && day != null && date != null && plate != null) {
                        exitList.add(new ExitFragment.ExitLog(timestamp, day, date, plate));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onItemClick(View view, int position) {
        if (getContext() != null) {
            //Toast.makeText(getContext(), "Exit on " + dateList.get(position) + " at " + timeList.get(position), Toast.LENGTH_SHORT).show();
        }
    }

    public static class ExitLog {
        public String time;
        public String day;
        public String date;
        String plate;

        public ExitLog(String time, String day, String date, String plate) {
            this.time = time;
            this.day = day;
            this.date = date;
            this.plate = plate;
        }
    }

    private class ExitAdapterAdmin extends RecyclerView.Adapter<ExitAdapterAdmin.ExitViewHolder> {
        ArrayList<ExitLog> exitLogs;

        ExitAdapterAdmin(Context context, ArrayList<ExitLog> exitLogs) {
            this.exitLogs = exitLogs;
        }

        @NonNull
        @Override
        public ExitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.logadmin, parent, false);
            return new ExitViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull ExitViewHolder holder, int position) {
            ExitLog exitLog = exitLogs.get(position);
            holder.time.setText(exitLog.time);
            holder.day.setText(exitLog.day);
            holder.date.setText(exitLog.date);
            holder.plate.setText(exitLog.plate);

            // Open detail view
            //holder.itemView.setOnClickListener(v -> showUserDetail(user));

        }

        @Override
        public int getItemCount() {
            return exitLogs.size();
        }

        class ExitViewHolder extends RecyclerView.ViewHolder {
            TextView time, day, date, plate;

            ExitViewHolder(@NonNull View itemView) {
                super(itemView);
                time = itemView.findViewById(R.id.textTime);
                day = itemView.findViewById(R.id.textDay);
                date = itemView.findViewById(R.id.textDate);
                plate = itemView.findViewById(R.id.textPlate);
            }
        }
    }
}