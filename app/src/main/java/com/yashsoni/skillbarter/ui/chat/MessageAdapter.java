package com.yashsoni.skillbarter.ui.chat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messageList;
    private final String currentUserId;

    public MessageAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_bubble, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        boolean isOut = message.getSenderId() != null && message.getSenderId().equals(currentUserId);

        if (isOut) {
            holder.container.setGravity(Gravity.END);
            holder.bubble.setBackgroundResource(R.drawable.bg_bubble_out);
            holder.tvMessage.setTextColor(0xFFFFFFFF);
            holder.tvTime.setTextColor(0xCCFFFFFF);
        } else {
            holder.container.setGravity(Gravity.START);
            holder.bubble.setBackgroundResource(R.drawable.bg_bubble_in);
            holder.tvMessage.setTextColor(0xFF1A1A2E);
            holder.tvTime.setTextColor(0xFF9CA3AF);
        }

        String text = message.getMessage();
        if (text != null && !text.trim().isEmpty()) {
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setText(text);
        } else {
            holder.tvMessage.setVisibility(View.GONE);
        }

        holder.tvTime.setText(formatTime(message.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private String formatTime(String createdAt) {
        if (createdAt == null || createdAt.isEmpty()) return "";
        try {
            long millis = new Date(createdAt).getTime();
            return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(millis));
        } catch (Exception e) {
            return "";
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container, bubble;
        TextView tvMessage, tvTime;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.layoutMessageContainer);
            bubble = itemView.findViewById(R.id.layoutBubble);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
