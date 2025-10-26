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

public class ExitFragment extends Fragment {

    exitAdapter adapter;
    private ArrayList<exitLog> exitList = new ArrayList<>();

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exit, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.exitList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new exitAdapter(getContext(), exitList);
        recyclerView.setAdapter(adapter);

        loadExit();
        return view;
    }

    private void loadExit() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        DatabaseReference exitRef = FirebaseDatabase.getInstance().getReference("exitLog");
        exitRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<exitLog> tempList = new ArrayList<>();

                for (DataSnapshot randomChildSnapshot : snapshot.getChildren()) {
                    String exitUid = randomChildSnapshot.child("UID").getValue(String.class);

                    if (uid.equals(exitUid)) {
                        String timestamp = randomChildSnapshot.child("timestamp").getValue(String.class);
                        String day = randomChildSnapshot.child("day").getValue(String.class);
                        String date = randomChildSnapshot.child("date").getValue(String.class);

                        if (timestamp != null && day != null && date != null) {
                            tempList.add(new exitLog(timestamp, day, date));
                        }
                    }
                }

                exitList.clear();
                for (int i = tempList.size() - 1; i >= 0; i--) {
                    exitList.add(tempList.get(i));
                }

                adapter.notifyDataSetChanged();

                if (exitList.isEmpty()) {
                    Toast.makeText(getContext(), "No exit logs found for your account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load exit logs: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onItemClick(View view, int position) {
        if (getContext() != null) {
        }
    }

    public class exitLog {
        String time;
        String day;
        String date;

        public exitLog(String time, String day, String date) {
            this.time = time;
            this.day = day;
            this.date = date;
        }
    }

    private class exitAdapter extends RecyclerView.Adapter<exitAdapter.exitViewHolder> {
        ArrayList<exitLog> exitLogs;

        exitAdapter(Context context, ArrayList<exitLog> exitLogs) {
            this.exitLogs = exitLogs;
        }

        @NonNull
        @Override
        public exitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.log, parent, false);
            return new exitViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull exitViewHolder holder, int position) {
            exitLog exitLog = exitLogs.get(position);
            holder.time.setText(exitLog.time);
            holder.day.setText(exitLog.day);
            holder.date.setText(exitLog.date);
        }

        @Override
        public int getItemCount() {
            return exitLogs.size();
        }

        class exitViewHolder extends RecyclerView.ViewHolder {
            TextView time, day, date;

            exitViewHolder(@NonNull View itemView) {
                super(itemView);
                time = itemView.findViewById(R.id.textTime);
                day = itemView.findViewById(R.id.textDay);
                date = itemView.findViewById(R.id.textDate);
            }
        }
    }
}