package com.yashsoni.skillbarter.ui.home;

import android.content.Context;
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
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.ui.profile.ProfileFragment;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnUserClickListener {
        void onRequestClick(User user);
        void onViewProfileClick(User user);
    }

    private final List<User> userList;
    private final OnUserClickListener listener;

    public UserAdapter(List<User> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        Context ctx = holder.itemView.getContext();

        holder.tvName.setText(user.getName());
        holder.tvLocation.setText(user.getLocation() != null ? "📍 " + user.getLocation() : "");

        // Rating
        double rating = user.getRating();
        if (rating > 0) {
            holder.tvRating.setVisibility(View.VISIBLE);
            holder.tvRating.setText(String.format("%.1f", rating));
        } else {
            holder.tvRating.setVisibility(View.GONE);
        }

        // Avatar
        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            holder.ivAvatar.setImageResource(ProfileFragment.getAvatarResource(user.getProfileImage()));
        } else {
            holder.ivAvatar.setImageResource(R.drawable.ic_profile);
        }

        // Teaches chips
        holder.chipGroupTeaches.removeAllViews();
        List<String> offered = user.getOfferedSkills();
        if (offered != null && !offered.isEmpty()) {
            for (String s : offered) {
                holder.chipGroupTeaches.addView(makeChip(ctx, s, true));
            }
        } else {
            holder.chipGroupTeaches.addView(makeChip(ctx, "Not listed", false));
        }

        // Wants chips
        holder.chipGroupWants.removeAllViews();
        List<String> wanted = user.getWantedSkills();
        if (wanted != null && !wanted.isEmpty()) {
            for (String s : wanted) {
                holder.chipGroupWants.addView(makeChip(ctx, s, false));
            }
        } else {
            holder.chipGroupWants.addView(makeChip(ctx, "Not listed", false));
        }

        holder.btnRequest.setOnClickListener(v -> {
            if (listener != null) listener.onRequestClick(user);
        });

        holder.btnViewProfile.setOnClickListener(v -> {
            if (listener != null) listener.onViewProfileClick(user);
        });
    }

    private Chip makeChip(Context ctx, String text, boolean filled) {
        Chip chip = new Chip(ctx);
        chip.setText(text);
        chip.setClickable(false);
        chip.setCheckable(false);
        chip.setTextSize(11f);
        if (filled) {
            chip.setChipBackgroundColorResource(R.color.chip_selected_bg);
            chip.setTextColor(ctx.getColor(R.color.primary));
        } else {
            chip.setChipBackgroundColorResource(R.color.chip_bg);
            chip.setTextColor(ctx.getColor(R.color.text_secondary));
        }
        return chip;
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLocation, tvRating;
        ImageView ivAvatar;
        ChipGroup chipGroupTeaches, chipGroupWants;
        Button btnRequest, btnViewProfile;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvLocation = itemView.findViewById(R.id.tvUserLocation);
            tvRating = itemView.findViewById(R.id.tvRating);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            chipGroupTeaches = itemView.findViewById(R.id.chipGroupTeaches);
            chipGroupWants = itemView.findViewById(R.id.chipGroupWants);
            btnRequest = itemView.findViewById(R.id.btnRequest);
            btnViewProfile = itemView.findViewById(R.id.btnViewProfile);
        }
    }
}
