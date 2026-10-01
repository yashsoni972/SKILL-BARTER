package com.yashsoni.skillbarter.ui.requests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.ExchangeRequest;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.utils.TimeFormat;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private final List<ExchangeRequest> requestList;
    private final OnRequestActionListener listener;
    private final String currentUserId;
    private final boolean isIncomingTab;

    public interface OnRequestActionListener {
        void onAccept(ExchangeRequest request);
        void onReject(ExchangeRequest request);
        void onChat(ExchangeRequest request, User partner);
    }

    public RequestAdapter(List<ExchangeRequest> requestList, boolean isIncomingTab, String currentUserId, OnRequestActionListener listener) {
        this.requestList = requestList;
        this.isIncomingTab = isIncomingTab;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_card, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        ExchangeRequest request = requestList.get(position);

        // Show the OTHER person. Previously this always used senderId, which on
        // the "Sent" tab is the logged-in user, so the card showed yourself.
        User other = request.getOtherUser(currentUserId);
        if (other != null && other.getName() != null && !other.getName().isEmpty()) {
            holder.tvName.setText(other.getName());
        } else {
            holder.tvName.setText("Skill Exchange User");
        }

        holder.tvSkills.setText(request.getOfferedSkill() + " ↔ " + request.getRequestedSkill());
        holder.tvWants.setText("Wants: " + request.getRequestedSkill());
        holder.tvTime.setText(TimeFormat.relative(request.getCreatedAt()));

        String status = request.getStatus() == null ? "pending" : request.getStatus().toLowerCase();
        boolean isPending = status.equals("pending");
        boolean isAccepted = status.equals("accepted");
        boolean canChat = isAccepted && other != null && other.getId() != null;

        // Status badge
        holder.tvStatus.setText(labelFor(status, isIncomingTab));
        int colorRes;
        if (isAccepted) {
            colorRes = R.color.accent_green;
        } else if (status.equals("rejected")) {
            colorRes = R.color.accent_red;
        } else if (status.equals("completed")) {
            colorRes = R.color.primary;
        } else {
            colorRes = R.color.text_secondary;
        }
        holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), colorRes));

        // WhatsApp-like rules:
        //  - Received + pending  -> Accept / Reject
        //  - Sent + pending      -> no buttons, just "Waiting for reply"
        //  - Accepted            -> Chat (both tabs)
        //  - Rejected/Completed  -> no buttons
        boolean showDecision = isIncomingTab && isPending;
        holder.layoutActions.setVisibility(showDecision || canChat ? View.VISIBLE : View.GONE);
        holder.btnAccept.setVisibility(showDecision ? View.VISIBLE : View.GONE);
        holder.btnReject.setVisibility(showDecision ? View.VISIBLE : View.GONE);
        holder.btnChat.setVisibility(canChat ? View.VISIBLE : View.GONE);

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onAccept(request);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onReject(request);
        });

        holder.btnChat.setOnClickListener(v -> {
            if (listener != null) listener.onChat(request, other);
        });
    }

    private String labelFor(String status, boolean isIncomingTab) {
        switch (status) {
            case "accepted":
                return "✓ Accepted - you can chat";
            case "rejected":
                return "✕ Rejected";
            case "completed":
                return "✓ Session completed";
            case "cancelled":
                return "Cancelled";
            default:
                return isIncomingTab ? "⏳ New request - tap Accept" : "⏳ Waiting for reply";
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSkills, tvWants, tvTime, tvStatus;
        LinearLayout layoutActions;
        Button btnAccept, btnReject, btnChat;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvSkills = itemView.findViewById(R.id.tvUserSkills);
            tvWants = itemView.findViewById(R.id.tvWants);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnChat = itemView.findViewById(R.id.btnChat);
        }
    }
}
