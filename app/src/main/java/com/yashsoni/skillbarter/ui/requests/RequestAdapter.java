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

import java.util.List;

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
        holder.tvTime.setText(request.getCreatedAt());

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
