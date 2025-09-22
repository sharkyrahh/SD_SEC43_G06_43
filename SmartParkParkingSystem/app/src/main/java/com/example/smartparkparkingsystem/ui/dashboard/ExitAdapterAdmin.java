package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartparkparkingsystem.R;

import java.util.List;

public class ExitAdapterAdmin extends RecyclerView.Adapter<ExitAdapterAdmin.ExitViewHolder> {

    private List<ExitFragment.ExitLog> exitList;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public ExitAdapterAdmin(Context context, List<ExitFragment.ExitLog> exitList) {
        this.mInflater = LayoutInflater.from(context);
        this.exitList = exitList;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ExitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.logadmin, parent, false);
        return new ExitViewHolder(view);
    }

    // binds the data to the TextViews in each row
    @Override
    public void onBindViewHolder(@NonNull ExitViewHolder holder, int position) {
        ExitFragment.ExitLog exitLog = exitList.get(position);
        holder.textDate.setText(exitLog.date);
        holder.textTime.setText(exitLog.time);
        holder.textPlate.setText(exitLog.plate);
        holder.textDay.setText(exitLog.day);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return exitList.size();
    }

    public void updateList(List<ExitFragment.ExitLog> newList) {
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