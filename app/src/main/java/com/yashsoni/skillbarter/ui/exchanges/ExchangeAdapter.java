package com.yashsoni.skillbarter.ui.exchanges;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.ExchangeSummary;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.ui.profile.ProfileFragment;
import com.yashsoni.skillbarter.utils.TimeFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists every exchange the member has, showing who it was with and which skill
 * moved in each direction. The Rate button only appears once the session is
 * actually completed.
 */
public class ExchangeAdapter extends RecyclerView.Adapter<ExchangeAdapter.ViewHolder> {

    public interface OnExchangeActionListener {
        void onChat(ExchangeSummary exchange, User partner);
        void onRate(ExchangeSummary exchange, User partner);
        void onDetails(ExchangeSummary exchange, User partner);
    }

    private final List<ExchangeSummary> exchanges = new ArrayList<>();
    private final List<String> reviewedUserIds = new ArrayList<>();
    private final OnExchangeActionListener listener;

    public ExchangeAdapter(OnExchangeActionListener listener) {
        this.listener = listener;
    }

    public void setExchanges(List<ExchangeSummary> newExchanges) {
        exchanges.clear();
        if (newExchanges != null) {
            exchanges.addAll(newExchanges);
        }
        notifyDataSetChanged();
    }

    /** Ids of partners already reviewed, so those rows show "Rated". */
    public void setReviewed(List<String> userIds) {
        reviewedUserIds.clear();
        if (userIds != null) {
            reviewedUserIds.addAll(userIds);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exchange, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExchangeSummary exchange = exchanges.get(position);
        User partner = exchange.getPartner();

        holder.tvPartnerName.setText(partner != null && partner.getName() != null
                ? partner.getName() : "Skill Exchange Partner");
        holder.tvSkills.setText(exchange.getSkillLine());

        // Partner avatar
        if (partner != null && partner.getProfileImage() != null && !partner.getProfileImage().isEmpty()) {
            String img = partner.getProfileImage();
            if (img.startsWith("content://") || img.startsWith("http://") || img.startsWith("https://")) {
                Glide.with(holder.itemView.getContext())
                        .load(img.startsWith("content://") ? Uri.parse(img) : img)
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile)
                        .into(holder.ivPartnerAvatar);
            } else {
                holder.ivPartnerAvatar.setImageResource(ProfileFragment.getAvatarResource(img));
            }
        } else {
            holder.ivPartnerAvatar.setImageResource(R.drawable.ic_profile);
        }

        // Status badge
        if (exchange.isCompleted()) {
            holder.tvStatus.setText("✓ Completed");
            holder.tvStatus.setTextColor(0xFF00BFA5);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
        } else {
            holder.tvStatus.setText("● Active");
            holder.tvStatus.setTextColor(0xFF6C63FF);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_active);
        }

        // Session date
        String when = TimeFormat.date(exchange.getSessionDate());
        if (when != null && !when.isEmpty()) {
            holder.tvSessionDate.setVisibility(View.VISIBLE);
            holder.tvSessionDate.setText("📅 " + when + (exchange.getSessionTime() != null ? "  🕐 " + exchange.getSessionTime() : ""));
        } else {
            holder.tvSessionDate.setVisibility(View.GONE);
        }

        boolean rated = partner != null && partner.getId() != null
                && reviewedUserIds.contains(partner.getId());
        holder.btnRate.setText(rated ? "Rated ✓" : "Rate ⭐");
        holder.btnRate.setEnabled(!rated && exchange.isCompleted());
        holder.btnChat.setEnabled(partner != null && partner.getId() != null);

        holder.btnChat.setOnClickListener(v -> {
            if (listener != null) listener.onChat(exchange, partner);
        });
        holder.btnRate.setOnClickListener(v -> {
            if (listener != null) listener.onRate(exchange, partner);
        });
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onDetails(exchange, partner);
        });
    }

    @Override
    public int getItemCount() {
        return exchanges.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvPartnerName, tvSkills, tvStatus, tvSessionDate;
        final ImageView ivPartnerAvatar;
        final Button btnChat, btnRate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPartnerAvatar = itemView.findViewById(R.id.ivPartnerAvatar);
            tvPartnerName = itemView.findViewById(R.id.tvPartnerName);
            tvSkills = itemView.findViewById(R.id.tvSkills);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvSessionDate = itemView.findViewById(R.id.tvSessionDate);
            btnChat = itemView.findViewById(R.id.btnChat);
            btnRate = itemView.findViewById(R.id.btnRate);
        }
    }
}