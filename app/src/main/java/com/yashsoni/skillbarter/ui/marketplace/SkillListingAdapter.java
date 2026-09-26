package com.yashsoni.skillbarter.ui.marketplace;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.SkillListing;
import com.yashsoni.skillbarter.ui.profile.ProfileFragment;

import java.util.List;

public class SkillListingAdapter extends RecyclerView.Adapter<SkillListingAdapter.ListingViewHolder> {

    private final List<SkillListing> listings;
    private final OnListingClickListener listener;

    public interface OnListingClickListener {
        void onLearnClick(SkillListing listing);
    }

    public SkillListingAdapter(List<SkillListing> listings, OnListingClickListener listener) {
        this.listings = listings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skill_listing, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        SkillListing listing = listings.get(position);
        holder.tvTitle.setText(listing.getTitle());
        holder.tvDescription.setText(listing.getDescription());
        holder.tvLevel.setText(listing.getLevel() != null ? listing.getLevel() : "Intermediate");
        holder.tvCredits.setText("💎 " + listing.getHourlyCredits() + " Credits / Hour");

        if (listing.getUserId() != null) {
            holder.tvTeacher.setText(listing.getUserId().getName() + " • ⭐ " + listing.getUserId().getRating());
            if (listing.getUserId().getProfileImage() != null && !listing.getUserId().getProfileImage().isEmpty()) {
                holder.ivAvatar.setImageResource(ProfileFragment.getAvatarResource(listing.getUserId().getProfileImage()));
            }
        } else {
            holder.tvTeacher.setText("Community Mentor • ⭐ 5.0");
        }

        holder.btnLearn.setOnClickListener(v -> {
            if (listener != null) listener.onLearnClick(listing);
        });
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    static class ListingViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTeacher, tvLevel, tvDescription, tvCredits;
        ImageView ivAvatar;
        Button btnLearn;

        ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
            tvLevel = itemView.findViewById(R.id.tvLevel);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCredits = itemView.findViewById(R.id.tvCredits);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            btnLearn = itemView.findViewById(R.id.btnLearn);
        }
    }
}
