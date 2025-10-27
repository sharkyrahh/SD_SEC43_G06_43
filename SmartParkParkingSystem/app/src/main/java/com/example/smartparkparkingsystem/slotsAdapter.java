package com.example.smartparkparkingsystem;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class slotsAdapter extends RecyclerView.Adapter<slotsAdapter.VH> {

    public interface Listener {
        void onSlotClick(ParkingSlot slot);
    }

    private final Context ctx;
    private final List<ParkingSlot> items;
    private final Listener listener;

    public slotsAdapter(Context ctx, List<ParkingSlot> items, Listener listener) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_slot, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ParkingSlot s = items.get(position);
        if (s == null) return;

        String name = s.getName() != null ? s.getName() : "—";
        String location = s.getLocation() != null ? s.getLocation() : "";
        String statusRaw = s.getStatus() != null ? s.getStatus().toLowerCase() : "unknown";
        String statusLabel = statusRaw.length() > 0 ? Character.toUpperCase(statusRaw.charAt(0)) + statusRaw.substring(1) : "Unknown";

        holder.tvSlotCode.setText(name);
        holder.tvSlotLocation.setText(location);
        holder.tvSlotStatus.setText(statusLabel);

        int badgeColor;
        int cardBg;
        switch (statusRaw) {
            case "available":
                badgeColor = 0xFF4CAF50; 
                cardBg = 0xFFE8F5E9;
                break;
            case "full":
                badgeColor = 0xFFE53935;
                cardBg = 0xFFFFEBEE;
                break;
            case "reserved":
                badgeColor = 0xFFFB8C00;
                cardBg = 0xFFFFF3E0;
                break;
            default:
                badgeColor = 0xFF9E9E9E;
                cardBg = 0xFFFFFFFF;
                break;
        }

        holder.tvSlotStatus.setTextColor(badgeColor);
        holder.cardView.setCardBackgroundColor(cardBg);

        holder.tvSlotCode.setTextColor(0xFF000000);
        holder.tvSlotLocation.setTextColor(0xFF777777);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSlotClick(s);
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView tvSlotCode, tvSlotLocation, tvSlotStatus;
        CardView cardView;

        public VH(@NonNull View v) {
            super(v);
            tvSlotCode = v.findViewById(R.id.tvSlotCode);
            tvSlotLocation = v.findViewById(R.id.tvSlotLocation);
            tvSlotStatus = v.findViewById(R.id.tvSlotStatus);
            cardView = (CardView) v;
        }
    }
}