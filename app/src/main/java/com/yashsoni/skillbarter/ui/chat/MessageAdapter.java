package com.yashsoni.skillbarter.ui.chat;

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

/** Renders a chat bubble, showing an inline image or a file row when one is attached. */
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

        if (isOut) {
            holder.container.setGravity(Gravity.END);
            holder.bubble.setBackgroundResource(R.drawable.bg_hero_banner);
            holder.textColor(holder.itemView.getContext().getColor(R.color.white));
            holder.fileMetaColor(holder.itemView.getContext().getColor(R.color.primary_light));
        } else {
            holder.container.setGravity(Gravity.START);
            holder.bubble.setBackgroundResource(R.drawable.bg_rounded_card);
            holder.textColor(holder.itemView.getContext().getColor(R.color.text_primary));
            holder.fileMetaColor(holder.itemView.getContext().getColor(R.color.text_secondary));
        }

        // A file-only message must not leave an empty line where the text sits.
        String text = message.getMessage();
        boolean hasText = text != null && !text.trim().isEmpty();
        holder.tvMessage.setVisibility(hasText ? View.VISIBLE : View.GONE);
        holder.tvMessage.setText(text);

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
            return createdAt;
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container, bubble, layoutFileRow;
        TextView tvMessage, tvTime, tvFileName, tvFileSize;
        ImageView ivAttachment;

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
        }

        void textColor(int color) {
            tvMessage.setTextColor(color);
            tvFileName.setTextColor(color);
        }

        void fileMetaColor(int color) {
            tvFileSize.setTextColor(color);
        }
    }
}