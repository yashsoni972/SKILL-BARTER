package com.yashsoni.skillbarter.ui.requests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.ExchangeRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private final List<ExchangeRequest> requestList;
    private final OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onAccept(ExchangeRequest request);
        void onReject(ExchangeRequest request);
    }

    public RequestAdapter(List<ExchangeRequest> requestList, OnRequestActionListener listener) {
        this.requestList = requestList;
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
        if (request.getSenderId() != null) {
            holder.tvName.setText(request.getSenderId().getName());
        } else {
            holder.tvName.setText("Skill Exchange User");
        }

        holder.tvSkills.setText(request.getOfferedSkill() + " ↔ " + request.getRequestedSkill());
        holder.tvWants.setText("Wants: " + request.getRequestedSkill());
        holder.tvTime.setText(formatTime(request.getCreatedAt()));

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onAccept(request);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onReject(request);
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    private String formatTime(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            long time = parser.parse(raw).getTime();
            long elapsed = System.currentTimeMillis() - time;
            if (elapsed < 60_000L) {
                return "Just now";
            }
            if (elapsed < 3_600_000L) {
                return (elapsed / 60_000L) + "m ago";
            }
            if (elapsed < 86_400_000L) {
                return (elapsed / 3_600_000L) + "h ago";
            }
            return new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(new Date(time));
        } catch (ParseException e) {
            return raw;
        }
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSkills, tvWants, tvTime;
        Button btnAccept, btnReject;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvSkills = itemView.findViewById(R.id.tvUserSkills);
            tvWants = itemView.findViewById(R.id.tvWants);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
