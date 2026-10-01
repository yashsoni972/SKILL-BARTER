package com.yashsoni.skillbarter.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.Conversation;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.ui.profile.ProfileFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<Conversation> conversations;
    private final OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(Conversation conversation);
    }

    public ChatAdapter(List<Conversation> conversations, OnChatClickListener listener) {
        this.conversations = conversations;
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
        Conversation conversation = conversations.get(position);
        User partner = conversation.getPartner();

        if (partner == null) {
            holder.tvName.setText("Unknown user");
            holder.tvLastMessage.setText("");
            holder.tvTime.setText("");
            return;
        }

        holder.tvName.setText(partner.getName() != null ? partner.getName() : "Skill Exchange User");

        // Real last message, with a "You:" prefix for messages we sent.
        String last = conversation.getLastMessage();
        if (last == null || last.isEmpty()) {
            holder.tvLastMessage.setText("Exchange accepted - say hello 👋");
        } else {
            holder.tvLastMessage.setText(conversation.isLastMessageMine() ? "You: " + last : last);
        }

        holder.tvTime.setText(formatTime(conversation.getLastMessageAt()));

        if (partner.getProfileImage() != null && !partner.getProfileImage().isEmpty()) {
            holder.ivAvatar.setImageResource(ProfileFragment.getAvatarResource(partner.getProfileImage()));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onChatClick(conversation);
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    private String formatTime(String raw) {
        if (raw == null || raw.isEmpty()) return "";
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            long time = parser.parse(raw).getTime();
            long elapsed = System.currentTimeMillis() - time;
            if (elapsed < 60_000L) return "now";
            if (elapsed < 3_600_000L) return (elapsed / 60_000L) + "m";
            if (elapsed < 86_400_000L) return (elapsed / 3_600_000L) + "h";
            if (elapsed < 604_800_000L) return (elapsed / 86_400_000L) + "d";
            return new SimpleDateFormat("d MMM", Locale.getDefault()).format(new Date(time));
        } catch (ParseException e) {
            return "";
        }
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
