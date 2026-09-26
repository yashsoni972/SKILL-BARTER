package com.yashsoni.skillbarter.ui.availability;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.databinding.ActivityAvailabilityBinding;

public class AvailabilityActivity extends AppCompatActivity {

    private ActivityAvailabilityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAvailabilityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnAddSlot.setOnClickListener(v -> {
            Toast.makeText(this, "✓ Time slot added: Friday 5:00 PM - 8:00 PM (Online)", Toast.LENGTH_LONG).show();
        });
    }
}
