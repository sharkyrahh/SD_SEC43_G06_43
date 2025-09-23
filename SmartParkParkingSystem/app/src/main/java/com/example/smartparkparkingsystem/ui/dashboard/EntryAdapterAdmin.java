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

public class EntryAdapterAdmin extends RecyclerView.Adapter<EntryAdapterAdmin.EntryViewHolder> {

    private List<EntryFragmentAdmin.EntryLog> entryList;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public EntryAdapterAdmin(Context context, List<EntryFragmentAdmin.EntryLog> entryList) {
        this.mInflater = LayoutInflater.from(context);
        this.entryList = entryList;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.logadmin, parent, false);
        return new EntryViewHolder(view);
    }

    // binds the data to the TextViews in each row
    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        EntryFragmentAdmin.EntryLog entryLog = entryList.get(position);
        holder.textDate.setText(entryLog.date);
        holder.textTime.setText(entryLog.time);
        holder.textPlate.setText(entryLog.plate);
        holder.textDay.setText(entryLog.day);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return entryList.size();
    }

    public void updateList(List<EntryFragmentAdmin.EntryLog> newList) {
        entryList = newList;
        notifyDataSetChanged();
    }

    // stores and recycles views as they are scrolled off screen
    public class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textTime, textPlate, textDay;

        EntryViewHolder(View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.textDate);
            textTime = itemView.findViewById(R.id.textTime);
            textPlate = itemView.findViewById(R.id.textPlate);
            textDay = itemView.findViewById(R.id.textDay);
        }
    }
}