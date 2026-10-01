package com.yashsoni.skillbarter.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.Notification;
import com.yashsoni.skillbarter.utils.TimeFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists real notifications from GET /notifications. Unread rows are tinted so the
 * list matches the red dot on the bell.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<Notification> notifications = new ArrayList<>();

    public void setNotifications(List<Notification> newNotifications) {
        notifications.clear();
        if (newNotifications != null) {
            notifications.addAll(newNotifications);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification n = notifications.get(position);

        holder.tvTitle.setText(n.getTitle());
        holder.tvMessage.setText(n.getMessage());
        holder.tvTime.setText(TimeFormat.relative(n.getCreatedAt()));

        holder.itemView.setBackgroundColor(holder.itemView.getContext().getColor(
                n.isRead() ? R.color.surface_white : R.color.bg_light));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvMessage;
        final TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}