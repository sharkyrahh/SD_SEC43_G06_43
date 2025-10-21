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

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder> {

    private List<Parking> parkingList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Parking parking);
    }

    public ParkingAdapter(List<Parking> parkingList, Context context, OnItemClickListener listener) {
        this.parkingList = parkingList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ParkingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_parking, parent, false);
        return new ParkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingViewHolder holder, int position) {
        Parking parking = parkingList.get(position);

        // Set both name and status
        holder.tvName.setText(parking.getName());
        holder.tvStatus.setText(parking.getStatus());

        // Set status color with null safety
        String status = parking.getStatus();
        if (status != null) {
            switch (status) {
                case "Available":
                    holder.tvStatus.setBackgroundResource(R.drawable.status_available);
                    break;
                case "Full":
                    holder.tvStatus.setBackgroundResource(R.drawable.status_full);
                    break;
                case "Reserved":
                    holder.tvStatus.setBackgroundResource(R.drawable.status_reserved);
                    break;
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(parking);
        });
    }

    @Override
    public int getItemCount() {
        return parkingList.size();
    }

    public static class ParkingViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus;

        public ParkingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSlotName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}