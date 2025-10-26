package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartparkparkingsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder> {

    private List<Parking> parkingList;
    private Context context;
    private OnItemClickListener listener;
    private DatabaseReference usersRef;

    public interface OnItemClickListener {
        void onItemClick(Parking parking);
    }

    public ParkingAdapter(List<Parking> parkingList, Context context, OnItemClickListener listener) {
        this.parkingList = parkingList;
        this.context = context;
        this.listener = listener;
        this.usersRef = FirebaseDatabase.getInstance("https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users");
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


        holder.tvName.setText(parking.getName());
        holder.tvStatus.setText(parking.getStatus());

        String status = parking.getStatus();
        if (status != null) {
            switch (status) {
                case "Available":
                    holder.tvStatus.setBackgroundResource(R.drawable.status_available);
                    holder.tvCar.setText("N/A");
                    break;
                case "Full":
                    holder.tvStatus.setBackgroundResource(R.drawable.status_full);
                    loadPlateNumber(parking.getReservedby(), holder.tvCar);
                    break;
                case "Reserved":
                    holder.tvStatus.setBackgroundResource(R.drawable.status_reserved);
                    loadPlateNumber(parking.getReservedby(),holder.tvCar);
                    break;
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(parking);
        });
    }

    private void loadPlateNumber(String userId, TextView tvCar){
        if (userId == null || userId.isEmpty()) {
            tvCar.setText("N/A");
            return;
        }

        usersRef.child(userId).child("plateNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String plateNumber = snapshot.getValue(String.class);
                if (plateNumber != null && !plateNumber.isEmpty()){
                    tvCar.setText(plateNumber);
                } else {
                    tvCar.setText("N/A");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvCar.setText("N/A");
            }
        });
    }

    @Override
    public int getItemCount() {
        return parkingList.size();
    }

    public static class ParkingViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus, tvCar;

        public ParkingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSlotName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCar = itemView.findViewById(R.id.tvCar);
        }
    }
}