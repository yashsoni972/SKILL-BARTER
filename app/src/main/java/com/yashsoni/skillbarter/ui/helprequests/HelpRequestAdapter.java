package com.yashsoni.skillbarter.ui.helprequests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.HelpRequest;
import com.yashsoni.skillbarter.ui.profile.ProfileFragment;

import java.util.List;

public class HelpRequestAdapter extends RecyclerView.Adapter<HelpRequestAdapter.HelpViewHolder> {

    private final List<HelpRequest> requests;
    private final OnHelpRequestListener listener;

    public interface OnHelpRequestListener {
        void onOfferHelp(HelpRequest request);
    }

    public HelpRequestAdapter(List<HelpRequest> requests, OnHelpRequestListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HelpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_help_request, parent, false);
        return new HelpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HelpViewHolder holder, int position) {
        HelpRequest request = requests.get(position);
        holder.tvTitle.setText(request.getTitle());
        holder.tvDescription.setText(request.getDescription());
        holder.tvMode.setText(request.getMode() != null ? request.getMode() : "Online");

        if (request.getUserId() != null) {
            holder.tvRequester.setText(request.getUserId().getName() + " • " + (request.getLevel() != null ? request.getLevel() : "Beginner"));
            if (request.getUserId().getProfileImage() != null && !request.getUserId().getProfileImage().isEmpty()) {
                holder.ivAvatar.setImageResource(ProfileFragment.getAvatarResource(request.getUserId().getProfileImage()));
            }
        } else {
            holder.tvRequester.setText("Community Member • Beginner");
        }

        holder.btnOfferHelp.setOnClickListener(v -> {
            if (listener != null) listener.onOfferHelp(request);
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class HelpViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvRequester, tvMode, tvDescription, tvTime;
        ImageView ivAvatar;
        Button btnOfferHelp;

        HelpViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRequester = itemView.findViewById(R.id.tvRequester);
            tvMode = itemView.findViewById(R.id.tvMode);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            btnOfferHelp = itemView.findViewById(R.id.btnOfferHelp);
        }
    }
}
