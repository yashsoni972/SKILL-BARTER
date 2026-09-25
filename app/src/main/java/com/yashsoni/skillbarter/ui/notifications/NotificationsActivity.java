package com.yashsoni.skillbarter.ui.notifications;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.databinding.ActivityNotificationsBinding;

public class NotificationsActivity extends AppCompatActivity {

    private ActivityNotificationsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(v -> finish());
    }
}
