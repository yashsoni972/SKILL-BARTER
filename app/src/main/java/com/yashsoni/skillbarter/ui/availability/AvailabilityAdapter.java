package com.yashsoni.skillbarter.ui.availability;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.Availability;

import java.util.List;

public class AvailabilityAdapter extends RecyclerView.Adapter<AvailabilityAdapter.SlotViewHolder> {

    public interface OnDeleteListener {
        void onDelete(Availability slot);
    }

    private final List<Availability> slots;
    private final OnDeleteListener listener;

    public AvailabilityAdapter(List<Availability> slots, OnDeleteListener listener) {
        this.slots = slots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_availability_slot, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        Availability slot = slots.get(position);
        holder.tvDay.setText(slot.getDay());
        holder.tvTime.setText(slot.getStartTime() + " - " + slot.getEndTime());
        holder.tvMode.setText(slot.getMode());
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(slot);
        });
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvTime, tvMode, btnDelete;

        SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMode = itemView.findViewById(R.id.tvMode);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
