package com.yashsoni.skillbarter.ui.exchanges;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.ExchangeSummary;
import com.yashsoni.skillbarter.data.model.User;
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
        holder.tvStatus.setText(statusLabel(exchange));

        boolean rated = partner != null && partner.getId() != null
                && reviewedUserIds.contains(partner.getId());
        holder.btnRate.setText(rated ? R.string.exchanges_rated : R.string.exchanges_rate);

        // Rating is only meaningful after a completed session, and only once.
        holder.btnRate.setEnabled(!rated && exchange.isCompleted());
        holder.btnChat.setEnabled(partner != null && partner.getId() != null);

        holder.btnChat.setOnClickListener(v -> {
            if (listener != null) listener.onChat(exchange, partner);
        });
        holder.btnRate.setOnClickListener(v -> {
            if (listener != null) listener.onRate(exchange, partner);
        });

        // Tapping the card itself opens the exchange details (date, medium,
        // mark as completed).
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onDetails(exchange, partner);
        });
    }

    private String statusLabel(ExchangeSummary exchange) {
        String when = TimeFormat.date(exchange.getSessionDate());
        String medium = exchange.getSessionLocation() == null
                ? "Online (Google Meet)" : exchange.getSessionLocation();

        if (exchange.isCompleted()) {
            return when.isEmpty() ? "Completed · " + medium : "Completed " + when + " · " + medium;
        }
        return when.isEmpty() ? "In progress · " + medium : "Scheduled " + when + " · " + medium;
    }

    @Override
    public int getItemCount() {
        return exchanges.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvPartnerName;
        final TextView tvSkills;
        final TextView tvStatus;
        final Button btnChat;
        final Button btnRate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPartnerName = itemView.findViewById(R.id.tvPartnerName);
            tvSkills = itemView.findViewById(R.id.tvSkills);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnChat = itemView.findViewById(R.id.btnChat);
            btnRate = itemView.findViewById(R.id.btnRate);
        }
    }
}