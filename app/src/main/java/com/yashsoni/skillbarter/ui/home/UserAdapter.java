package com.yashsoni.skillbarter.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;
    private final OnUserClickListener listener;

    public interface OnUserClickListener {
        void onRequestClick(User user);
    }

    public UserAdapter(List<User> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvName.setText(user.getName());

        String skills = "";
        if (user.getOfferedSkills() != null && !user.getOfferedSkills().isEmpty()) {
            skills = String.join(" • ", user.getOfferedSkills());
        } else {
            skills = "Skill Exchange Member";
        }
        holder.tvSkills.setText(skills);
        holder.tvLocation.setText("📍 " + user.getLocation());

        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            holder.ivAvatar.setImageResource(com.yashsoni.skillbarter.ui.profile.ProfileFragment.getAvatarResource(user.getProfileImage()));
        }

        holder.btnRequest.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRequestClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSkills, tvLocation;
        ImageView ivAvatar;
        Button btnRequest;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvSkills = itemView.findViewById(R.id.tvUserSkills);
            tvLocation = itemView.findViewById(R.id.tvUserLocation);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            btnRequest = itemView.findViewById(R.id.btnRequest);
        }
    }
}
