package com.yashsoni.skillbarter.ui.chat;

import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.Attachment;
import com.yashsoni.skillbarter.data.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public interface OnAttachmentClick {
        void onAttachmentClicked(Attachment attachment);
    }

    private final List<Message> messageList;
    private final String currentUserId;
    private final OnAttachmentClick attachmentClick;

    public MessageAdapter(List<Message> messageList, String currentUserId, OnAttachmentClick attachmentClick) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.attachmentClick = attachmentClick;
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
        Attachment attachment = message.getAttachment();
        boolean isOut = message.getSenderId() != null && message.getSenderId().equals(currentUserId);

        android.content.Context ctx = holder.itemView.getContext();

        if (isOut) {
            // Sent — indigo bubble, right aligned
            holder.container.setGravity(Gravity.END);
            holder.bubble.setBackgroundResource(R.drawable.bg_bubble_out);
            holder.tvMessage.setTextColor(0xFFFFFFFF);
            holder.tvMessage.setLinkTextColor(0xFFBFDBFE);
            holder.tvTime.setTextColor(0xCCFFFFFF);
            holder.tvFileName.setTextColor(0xFFFFFFFF);
            holder.tvFileSize.setTextColor(0xCCFFFFFF);
            holder.ivFileIcon.setColorFilter(0xFFFFFFFF, PorterDuff.Mode.SRC_IN);
        } else {
            // Received — white bubble, left aligned
            holder.container.setGravity(Gravity.START);
            holder.bubble.setBackgroundResource(R.drawable.bg_bubble_in);
            holder.tvMessage.setTextColor(0xFF0F172A);
            holder.tvMessage.setLinkTextColor(0xFF4F46E5);
            holder.tvTime.setTextColor(0xFF94A3B8);
            holder.tvFileName.setTextColor(0xFF0F172A);
            holder.tvFileSize.setTextColor(0xFF64748B);
            holder.ivFileIcon.setColorFilter(0xFF4F46E5, PorterDuff.Mode.SRC_IN);
        }

        String text = message.getMessage();
        boolean hasText = text != null && !text.trim().isEmpty();
        holder.tvMessage.setVisibility(hasText ? View.VISIBLE : View.GONE);
        if (hasText) holder.tvMessage.setText(text);

        holder.tvTime.setText(formatTime(message.getCreatedAt()));

        if (attachment == null) {
            holder.ivAttachment.setVisibility(View.GONE);
            holder.layoutFileRow.setVisibility(View.GONE);
            holder.bubble.setOnClickListener(null);
            return;
        }

        if (attachment.isImage() && attachment.getDownloadUrl() != null) {
            holder.ivAttachment.setVisibility(View.VISIBLE);
            holder.layoutFileRow.setVisibility(View.GONE);
            Glide.with(holder.ivAttachment)
                    .load(attachment.getDownloadUrl())
                    .centerCrop()
                    .into(holder.ivAttachment);
            holder.bubble.setOnClickListener(v -> {
                if (attachmentClick != null) attachmentClick.onAttachmentClicked(attachment);
            });
        } else {
            holder.ivAttachment.setVisibility(View.GONE);
            Glide.with(holder.ivAttachment).clear(holder.ivAttachment);
            holder.layoutFileRow.setVisibility(View.VISIBLE);
            holder.tvFileName.setText(attachment.getFilename());
            holder.tvFileSize.setText(attachment.humanSize());
            holder.bubble.setOnClickListener(v -> {
                if (attachmentClick != null) attachmentClick.onAttachmentClicked(attachment);
            });
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private String formatTime(String createdAt) {
        if (createdAt == null || createdAt.isEmpty()) return "";
        try {
            long millis = new java.util.Date(createdAt).getTime();
            return new java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(new java.util.Date(millis));
        } catch (Exception e) {
            return "";
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container, bubble, layoutFileRow;
        TextView tvMessage, tvTime, tvFileName, tvFileSize;
        ImageView ivAttachment, ivFileIcon;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.layoutMessageContainer);
            bubble = itemView.findViewById(R.id.layoutBubble);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivAttachment = itemView.findViewById(R.id.ivAttachment);
            layoutFileRow = itemView.findViewById(R.id.layoutFileRow);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            ivFileIcon = itemView.findViewById(R.id.ivFileIcon);
        }
    }
}
