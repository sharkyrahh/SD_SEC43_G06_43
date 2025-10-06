package com.example.smartparkparkingsystem;

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

/**
 * SlotsAdapter - RecyclerView adapter to display parking slots.
 * - Colors/status badges handled here
 * - Uses a Listener to notify Activity of clicks
 */
public abstract class StatusActivity extends RecyclerView.Adapter<StatusActivity.SlotViewHolder> {

    public interface Listener {
        void onSlotClick(ParkingSlot slot);
    }

    private final List<ParkingSlot> slotList;
    private final Listener listener;

    public StatusActivity(List<ParkingSlot> slotList, Listener listener) {
        this.slotList = slotList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, parent, false);
        return new SlotViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        ParkingSlot slot = slotList.get(position);
        if (slot == null) return;

        // null-safe getters (assumes ParkingSlot has getCode/getLocation/getStatus or public fields)
        String code = slot.getCode() != null ? slot.getCode() : "—";
        String location = slot.getLocation() != null ? slot.getLocation() : "";
        String statusRaw = slot.getStatus() != null ? slot.getStatus().toLowerCase() : "unknown";
        String statusLabel = statusRaw.length() > 0 ? statusRaw.substring(0,1).toUpperCase() + statusRaw.substring(1) : "Unknown";

        holder.tvSlotCode.setText(code);
        holder.tvSlotLocation.setText(location);
        holder.tvSlotStatus.setText(statusLabel);

        // choose colors by status
        int bgColor;
        int textColor = 0xFFFFFFFF; // white for badge text if using colored bg
        switch (statusRaw) {
            case "available":
                bgColor = 0xFF4CAF50; // green
                break;
            case "reserved":
                bgColor = 0xFFFB8C00; // orange
                break;
            case "occupied":
                bgColor = 0xFFE53935; // red
                break;
            case "maintenance":
                bgColor = 0xFF9E9E9E; // grey
                break;
            default:
                bgColor = 0xFF9E9E9E;
                textColor = 0xFF000000; // black text on light bg fallback
                break;
        }

        // Try tinting background shape (badge_bg should be a <shape> so it's a GradientDrawable)
        if (holder.tvSlotStatus.getBackground() instanceof GradientDrawable) {
            GradientDrawable gd = (GradientDrawable) holder.tvSlotStatus.getBackground();
            gd.setColor(bgColor);
            holder.tvSlotStatus.setTextColor(textColor);
        } else {
            // fallback: set text color only
            holder.tvSlotStatus.setTextColor(bgColor);
        }

        // Card background subtle tint depending on status (optional)
        int cardBg;
        switch (statusRaw) {
            case "available": cardBg = 0xFFE8F5E9; break; // light green
            case "reserved":  cardBg = 0xFFFFF3E0; break; // light orange
            case "occupied":  cardBg = 0xFFFFEBEE; break; // light red
            default:          cardBg = 0xFFFFFFFF; break;
        }
        holder.cardView.setCardBackgroundColor(cardBg);

        // item click -> use listener; also show a quick toast via item context
        holder.itemView.setOnClickListener(v -> {
            // show quick toast
            Toast.makeText(holder.itemView.getContext(),
                    code + " — " + statusLabel,
                    Toast.LENGTH_SHORT).show();

            // notify activity to handle action (reserve / show details)
            if (listener != null) listener.onSlotClick(slot);
        });
    }

    @Override
    public int getItemCount() {
        return slotList != null ? slotList.size() : 0;
    }

    // ViewHolder
    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvSlotCode, tvSlotLocation, tvSlotStatus;
        CardView cardView;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSlotCode = itemView.findViewById(R.id.tvSlotCode);
            tvSlotLocation = itemView.findViewById(R.id.tvSlotLocation);
            tvSlotStatus = itemView.findViewById(R.id.tvSlotStatus);
            cardView = itemView.findViewById(R.id.card_view_user); // ensure item_slot.xml or item_parking_slot.xml has this id
        }
    }
}
