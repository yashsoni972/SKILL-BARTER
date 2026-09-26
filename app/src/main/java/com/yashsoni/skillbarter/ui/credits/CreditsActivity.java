package com.yashsoni.skillbarter.ui.credits;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.databinding.ActivityCreditsBinding;

public class CreditsActivity extends AppCompatActivity {

    private ActivityCreditsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreditsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(v -> finish());
    }
}
