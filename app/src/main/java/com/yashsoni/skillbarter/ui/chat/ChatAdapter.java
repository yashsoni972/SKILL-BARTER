package com.yashsoni.skillbarter.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.User;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<User> partners;
    private final OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(User partner);
    }

    public ChatAdapter(List<User> partners, OnChatClickListener listener) {
        this.partners = partners;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_preview, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        User partner = partners.get(position);
        holder.tvName.setText(partner.getName());
        holder.tvLastMessage.setText("Sure! Let's plan for this weekend.");
        holder.tvTime.setText("10:30 AM");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onChatClick(partner);
        });
    }

    @Override
    public int getItemCount() {
        return partners.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLastMessage, tvTime;
        ImageView ivAvatar;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }
    }
}
