package com.yashsoni.skillbarter.ui.progress;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.databinding.ActivityProgressBinding;

public class ProgressActivity extends AppCompatActivity {

    private ActivityProgressBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProgressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(v -> finish());
    }
}
