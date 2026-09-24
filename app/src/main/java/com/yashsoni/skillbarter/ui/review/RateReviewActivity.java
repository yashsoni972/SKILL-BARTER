package com.yashsoni.skillbarter.ui.review;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivityRateReviewBinding;

public class RateReviewActivity extends AppCompatActivity {

    private ActivityRateReviewBinding binding;
    private User partnerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRateReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        partnerUser = (User) getIntent().getSerializableExtra("partnerUser");
        if (partnerUser == null) {
            partnerUser = new User("u1", "Riya Sharma", "riya@example.com", "Ahmedabad", "UI/UX Designer", 4.8, 5);
        }

        binding.tvUserName.setText(partnerUser.getName());

        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnSubmit.setOnClickListener(v -> {
            float rating = binding.ratingBar.getRating();
            Toast.makeText(this, "⭐ Review submitted! Thank you (" + rating + " stars)", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
