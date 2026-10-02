package com.yashsoni.skillbarter.ui.review;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.data.model.Review;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivityRateReviewBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.utils.SystemBars;

/**
 * Submits a real review to POST /reviews. The screen previously only displayed
 * the partner's name and never called the API, so ratings were silently lost and
 * the partner's average score never changed.
 */
public class RateReviewActivity extends AppCompatActivity {

    private ActivityRateReviewBinding binding;
    private SkillBarterRepository repository;
    private User partnerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRateReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        partnerUser = (User) getIntent().getSerializableExtra("partnerUser");
        if (partnerUser == null || partnerUser.getId() == null) {
            Toast.makeText(this, "No partner to review", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.tvUserName.setText(partnerUser.getName());
        binding.tvUserSkills.setText(skillsOf(partnerUser));

        binding.ivBack.setOnClickListener(v -> finish());
        binding.btnSubmit.setOnClickListener(v -> submit());
    }

    /** The partner's own skill list, or a neutral line when it is unknown. */
    private String skillsOf(User user) {
        if (user.getOfferedSkills() == null || user.getOfferedSkills().isEmpty()) {
            return "Skill exchange partner";
        }
        return String.join(" • ", user.getOfferedSkills());
    }

    private void submit() {
        int rating = (int) binding.ratingBar.getRating();
        if (rating < 1) {
            Toast.makeText(this, "Please choose a star rating first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = binding.etReview.getText().toString().trim();

        binding.btnSubmit.setEnabled(false);
        repository.submitReview(partnerUser.getId(), rating, comment, new SkillBarterRepository.DataCallback<Review>() {
            @Override
            public void onSuccess(Review review) {
                if (isFinishing() || isDestroyed()) return;
                Toast.makeText(RateReviewActivity.this, "Thanks for your review!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) return;
                binding.btnSubmit.setEnabled(true);
                Toast.makeText(RateReviewActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}