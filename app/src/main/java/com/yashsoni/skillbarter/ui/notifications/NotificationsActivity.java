package com.yashsoni.skillbarter.ui.notifications;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.Notification;
import com.yashsoni.skillbarter.databinding.ActivityNotificationsBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;

import com.yashsoni.skillbarter.utils.SystemBars;
import java.util.List;

/**
 * Shows the member's real notification feed. The list used to be three
 * hardcoded strings in the layout file that never changed and never reflected
 * anything that happened in the app.
 *
 * <p>Opening the screen marks everything as read, which is also what clears the
 * red dot on the bell.
 */
public class NotificationsActivity extends AppCompatActivity {

    private ActivityNotificationsBinding binding;
    private SkillBarterRepository repository;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        adapter = new NotificationAdapter();
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotifications.setAdapter(adapter);

        binding.ivBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void loadNotifications() {
        repository.fetchNotifications(new SkillBarterRepository.DataCallback<List<Notification>>() {
            @Override
            public void onSuccess(List<Notification> notifications) {
                if (isFinishing() || isDestroyed()) return;

                boolean empty = notifications == null || notifications.isEmpty();
                binding.tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                binding.rvNotifications.setVisibility(empty ? View.GONE : View.VISIBLE);
                adapter.setNotifications(notifications);

                markAllRead();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) return;
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.rvNotifications.setVisibility(View.GONE);
                binding.tvEmpty.setText(message);
                Toast.makeText(NotificationsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Clears the badge once the user has actually seen the feed. */
    private void markAllRead() {
        repository.markNotificationsRead(new SkillBarterRepository.DataCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject ignored) {
                // The badge is refreshed by the home screen on its next resume.
            }

            @Override
            public void onError(String message) {
                // Non-critical: the list is already correct on screen.
            }
        });
    }
}