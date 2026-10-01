package com.yashsoni.skillbarter.ui.exchange;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.Session;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivityExchangeDetailsBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.review.RateReviewActivity;
import com.yashsoni.skillbarter.utils.SessionManager;
import com.yashsoni.skillbarter.utils.TimeFormat;

import java.util.List;

public class ExchangeDetailsActivity extends AppCompatActivity {

    // Exchanges always happen over Google Meet, so the medium is never a choice.
    private static final String MEDIUM = "Online (Google Meet)";

    private ActivityExchangeDetailsBinding binding;
    private SkillBarterRepository repository;
    private User partnerUser;
    private String requestId;
    private String requestedSkill;
    private String offeredSkill;
    private String direction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExchangeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        partnerUser = (User) getIntent().getSerializableExtra("partnerUser");
        if (partnerUser == null) {
            Toast.makeText(this, "No exchange partner selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        requestId = getIntent().getStringExtra("requestId");
        requestedSkill = getIntent().getStringExtra("requestedSkill");
        offeredSkill = getIntent().getStringExtra("offeredSkill");
        direction = getIntent().getStringExtra("direction");

        if (requestId == null || requestId.isEmpty()) {
            Toast.makeText(this, "No exchange found for this chat. Open the request to schedule a session.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        User currUser = new SessionManager(this).getUser();
        if (currUser != null) {
            binding.tvUser1Name.setText(currUser.getName());
        }
        binding.tvUser2Name.setText(partnerUser.getName());

        // Date, time and medium come from the stored session. They used to be
        // fixed strings ("Sat, 27 Sep 2026", "Online (Google Meet)") that were the
        // same for every exchange.
        showNotScheduled();
        loadSessionDetails();

        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnMarkCompleted.setOnClickListener(v -> askForDurationThenReview());
    }

    private void showNotScheduled() {
        binding.tvDate.setText(getString(R.string.exchange_not_scheduled));
        binding.tvTime.setText("");
        binding.tvLocation.setText(getString(R.string.exchange_location_label, MEDIUM));
    }

    /**
     * Finds the session attached to this exchange so the screen shows when the
     * exchange actually happens and over which medium.
     */
    private void loadSessionDetails() {
        repository.fetchSessions(new SkillBarterRepository.DataCallback<List<Session>>() {
            @Override
            public void onSuccess(List<Session> sessions) {
                if (isFinishing() || isDestroyed() || binding == null) return;

                Session match = null;
                if (sessions != null) {
                    for (Session s : sessions) {
                        if (requestId != null && requestId.equals(s.getRequestId())) {
                            match = s;
                            break;
                        }
                    }
                }
                if (match == null) return;

                String date = TimeFormat.date(match.getDate());
                if (!date.isEmpty()) {
                    binding.tvDate.setText(getString(R.string.exchange_date_label, date));
                }
                String time = match.getTime();
                if (time != null && !time.isEmpty()) {
                    binding.tvTime.setText(getString(R.string.exchange_time_label, time));
                }
                binding.tvLocation.setText(getString(R.string.exchange_location_label,
                        match.getLocation() != null ? match.getLocation() : MEDIUM));
            }

            @Override
            public void onError(String message) {
                // Keep the unscheduled defaults; the screen stays usable.
            }
        });
    }

    /**
     * A session's real duration is what makes "hours taught / learned" on the
     * progress dashboard meaningful, so capture it before the review screen.
     */
    private void askForDurationThenReview() {
        String[] options = {"0.5", "1", "1.5", "2", "3"};
        String[] labels = {"30 min", "1 hour", "1.5 hours", "2 hours", "3 hours"};

        new AlertDialog.Builder(this)
                .setTitle("How long was the session?")
                .setItems(labels, (dialog, which) -> recordDuration(Double.parseDouble(options[which])))
                .setNegativeButton("Skip", (dialog, which) -> openReview())
                .show();
    }

    private void recordDuration(double hours) {
        // Whether this was taught or learned decides which progress bar moves,
        // so it must be answered rather than assumed.
        String[] roles = {"I taught this session", "I learned this session"};
        new AlertDialog.Builder(this)
                .setTitle("Your role")
                .setItems(roles, (dialog, which) -> saveSession(hours, which == 0))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveSession(double hours, boolean iTaught) {
        binding.btnMarkCompleted.setEnabled(false);
        repository.completeSession(requestId, hours, skillForProgress(iTaught), iTaught,
                new SkillBarterRepository.SessionCallback() {
                    @Override
                    public void onSuccess(Session session, int creditBalance, int creditChange) {
                        if (isFinishing() || isDestroyed()) return;
                        showCreditResult(creditBalance, creditChange);
                        openReview();
                    }

                    @Override
                    public void onError(String message) {
                        if (isFinishing() || isDestroyed()) return;
                        binding.btnMarkCompleted.setEnabled(true);
                        Toast.makeText(ExchangeDetailsActivity.this,
                                "Session not saved: " + message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /** Confirms the credit movement the session just caused. */
    private void showCreditResult(int balance, int change) {
        if (change == 0) return;
        String text = change > 0
                ? "+" + change + " credits earned. New balance: " + balance
                : change + " credits used. New balance: " + balance;
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void openReview() {
        Intent intent = new Intent(ExchangeDetailsActivity.this, RateReviewActivity.class);
        intent.putExtra("partnerUser", partnerUser);
        startActivity(intent);
        finish();
    }

    /**
     * The skill this session was actually about. If I taught, it is the skill I
     * offered; if I learned, it is the skill the partner offered. Falls back to
     * the partner's first listed skill only when the request carried no skills.
     */
    private String skillForProgress(boolean iTaught) {
        boolean iAmSender = !"incoming".equals(direction);
        String teaching = iAmSender ? offeredSkill : requestedSkill;
        String learning = iAmSender ? requestedSkill : offeredSkill;
        String chosen = iTaught ? teaching : learning;

        if (chosen != null && !chosen.trim().isEmpty()) {
            return chosen;
        }
        if (partnerUser.getOfferedSkills() != null && !partnerUser.getOfferedSkills().isEmpty()) {
            return partnerUser.getOfferedSkills().get(0);
        }
        return "Skill exchange";
    }
}
