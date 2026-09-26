package com.yashsoni.skillbarter.ui.discover;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.MatchResult;
import com.yashsoni.skillbarter.data.model.Skill;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.ui.profile.ProfileFragment;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private final List<MatchResult> matchList;
    private final OnMatchActionListener listener;

    public interface OnMatchActionListener {
        void onViewProfile(User user);
        void onExchange(User user);
    }

    public MatchAdapter(List<MatchResult> matchList, OnMatchActionListener listener) {
        this.matchList = matchList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        MatchResult match = matchList.get(position);
        holder.tvName.setText(match.getName());
        holder.tvLocation.setText("📍 " + (match.getLocation() != null ? match.getLocation() : "Gujarat, India"));
        holder.tvRating.setText(String.format("%.1f", match.getRating()));
        holder.tvMatchPercentage.setText(match.getMatchPercentage() + "% 🔥");

        if (match.getProfileImage() != null && !match.getProfileImage().isEmpty()) {
            holder.ivProfile.setImageResource(ProfileFragment.getAvatarResource(match.getProfileImage()));
        } else {
            holder.ivProfile.setImageResource(R.drawable.ic_profile);
        }

        // Bind CAN TEACH Chips
        holder.chipOffers.removeAllViews();
        if (match.getOffers() != null) {
            for (Skill s : match.getOffers()) {
                Chip chip = new Chip(holder.itemView.getContext());
                chip.setText(s.getSkillName());
                holder.chipOffers.addView(chip);
            }
        }

        // Bind WANTS TO LEARN Chips
        holder.chipWants.removeAllViews();
        if (match.getWants() != null) {
            for (Skill s : match.getWants()) {
                Chip chip = new Chip(holder.itemView.getContext());
                chip.setText(s.getSkillName());
                holder.chipWants.addView(chip);
            }
        }

        User userObj = match.toUser();

        holder.btnViewProfile.setOnClickListener(v -> {
            if (listener != null) listener.onViewProfile(userObj);
        });

        holder.btnExchange.setOnClickListener(v -> {
            if (listener != null) listener.onExchange(userObj);
        });
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvName, tvLocation, tvRating, tvMatchPercentage;
        ChipGroup chipOffers, chipWants;
        Button btnViewProfile, btnExchange;

        MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvMatchPercentage = itemView.findViewById(R.id.tvMatchPercentage);
            chipOffers = itemView.findViewById(R.id.chipOffers);
            chipWants = itemView.findViewById(R.id.chipWants);
            btnViewProfile = itemView.findViewById(R.id.btnViewProfile);
            btnExchange = itemView.findViewById(R.id.btnExchange);
        }
    }
}
