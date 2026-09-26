package com.yashsoni.skillbarter.ui.badges;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.Badge;

import java.util.List;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder> {

    private final List<Badge> badges;

    public BadgeAdapter(List<Badge> badges) {
        this.badges = badges;
    }

    @NonNull
    @Override
    public BadgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_badge, parent, false);
        return new BadgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeViewHolder holder, int position) {
        Badge badge = badges.get(position);
        holder.tvIcon.setText(badge.getIcon() != null ? badge.getIcon() : "🏆");
        holder.tvTitle.setText(badge.getTitle());

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "🏆 " + badge.getTitle() + ": " + badge.getDescription(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    static class BadgeViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvTitle;

        BadgeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvBadgeIcon);
            tvTitle = itemView.findViewById(R.id.tvBadgeTitle);
        }
    }
}
