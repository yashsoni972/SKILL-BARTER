package com.yashsoni.skillbarter.ui.requests;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.data.model.ExchangeRequest;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivitySendRequestBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.utils.SessionManager;

public class SendRequestActivity extends AppCompatActivity {

    private ActivitySendRequestBinding binding;
    private SkillBarterRepository repository;
    private User targetUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        targetUser = (User) getIntent().getSerializableExtra("targetUser");
        if (targetUser == null) {
            finish();
            return;
        }

        binding.tvUserName.setText(targetUser.getName());
        binding.tvUserSkills.setText(String.join(" • ", targetUser.getOfferedSkills()));
        binding.tvUserLocation.setText("📍 " + targetUser.getLocation());

        String want = (targetUser.getOfferedSkills() != null && !targetUser.getOfferedSkills().isEmpty())
                ? targetUser.getOfferedSkills().get(0) : "Graphic Design";
        binding.tvWantSkill.setText("🎨 " + want);

        User currUser = new SessionManager(this).getUser();
        String teach = (currUser != null && currUser.getOfferedSkills() != null && !currUser.getOfferedSkills().isEmpty())
                ? String.join(", ", currUser.getOfferedSkills()) : "HTML & CSS, JavaScript";
        binding.tvTeachSkill.setText("💻 " + teach);

        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnSendRequest.setOnClickListener(v -> {
            String msg = binding.etMessage.getText().toString().trim();
            if (targetUser.getId() == null || targetUser.getId().isEmpty()) {
                Toast.makeText(this, "Cannot send request: partner account is not synced to the cloud database.", Toast.LENGTH_LONG).show();
                return;
            }

            binding.btnSendRequest.setEnabled(false);
            repository.sendExchangeRequest(targetUser.getId(), teach, want, msg, new SkillBarterRepository.DataCallback<ExchangeRequest>() {
                @Override
                public void onSuccess(ExchangeRequest data) {
                    Toast.makeText(SendRequestActivity.this, "Exchange Request Sent Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String message) {
                    binding.btnSendRequest.setEnabled(true);
                    Toast.makeText(SendRequestActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
