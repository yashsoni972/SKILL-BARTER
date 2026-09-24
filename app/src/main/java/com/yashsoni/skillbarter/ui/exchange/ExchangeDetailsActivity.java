package com.yashsoni.skillbarter.ui.exchange;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivityExchangeDetailsBinding;
import com.yashsoni.skillbarter.ui.review.RateReviewActivity;
import com.yashsoni.skillbarter.utils.SessionManager;

public class ExchangeDetailsActivity extends AppCompatActivity {

    private ActivityExchangeDetailsBinding binding;
    private User partnerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExchangeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        partnerUser = (User) getIntent().getSerializableExtra("partnerUser");
        if (partnerUser == null) {
            partnerUser = new User("u1", "Riya Sharma", "riya@example.com", "Ahmedabad", "UI/UX Designer", 4.8, 5);
        }

        User currUser = new SessionManager(this).getUser();
        if (currUser != null) {
            binding.tvUser1Name.setText(currUser.getName());
        }
        binding.tvUser2Name.setText(partnerUser.getName());

        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnMarkCompleted.setOnClickListener(v -> {
            Intent intent = new Intent(ExchangeDetailsActivity.this, RateReviewActivity.class);
            intent.putExtra("partnerUser", partnerUser);
            startActivity(intent);
            finish();
        });
    }
}
