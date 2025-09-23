package com.example.smartparkparkingsystem.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartparkparkingsystem.R;
import com.example.smartparkparkingsystem.ui.dashboard.ExitFragmentAdmin;

import java.util.List;

public class ExitAdapter extends RecyclerView.Adapter<ExitAdapter.ExitViewHolder> {

    private List<ExitFragmentAdmin.ExitLog> exitList;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public ExitAdapter(Context context, List<ExitFragmentAdmin.ExitLog> exitList) {
        this.mInflater = LayoutInflater.from(context);
        this.exitList = exitList;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ExitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.log, parent, false);
        return new ExitViewHolder(view);
    }

    // binds the data to the TextViews in each row
    @Override
    public void onBindViewHolder(@NonNull ExitViewHolder holder, int position) {
        ExitFragmentAdmin.ExitLog exitLog = exitList.get(position);
        holder.textDate.setText(exitLog.date);
        holder.textTime.setText(exitLog.time);
        holder.textDay.setText(exitLog.day);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return exitList.size();
    }

    public void updateList(List<ExitFragmentAdmin.ExitLog> newList) {
        exitList = newList;
        notifyDataSetChanged();
    }

    // stores and recycles views as they are scrolled off screen
    public class ExitViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textTime, textPlate, textDay;

        ExitViewHolder(View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.textDate);
            textTime = itemView.findViewById(R.id.textTime);
            textPlate = itemView.findViewById(R.id.textPlate);
            textDay = itemView.findViewById(R.id.textDay);
        }
    }
}