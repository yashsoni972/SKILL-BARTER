package com.yashsoni.skillbarter.ui.requests;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.ExchangeRelationship;
import com.yashsoni.skillbarter.data.model.ExchangeRequest;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivitySendRequestBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.chat.ChatActivity;
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

        binding.btnSendRequest.setOnClickListener(v -> send(teach, want));

        checkExistingExchange();
    }

    /**
     * A pair of members shares one exchange. If this person already has a live
     * exchange with the current user, the form is replaced with the right action
     * (open the chat, or wait for their reply) instead of letting the user type a
     * message that the server will reject.
     */
    private void checkExistingExchange() {
        if (targetUser.getId() == null || targetUser.getId().isEmpty()) {
            return;
        }

        repository.fetchRelationship(targetUser.getId(),
                new SkillBarterRepository.DataCallback<ExchangeRelationship>() {
                    @Override
                    public void onSuccess(ExchangeRelationship relationship) {
                        if (isFinishing() || isDestroyed()) return;

                        if (relationship == null) return;

                        if (relationship.canChat()) {
                            openChat(relationship.getRequestId());
                        } else if (!relationship.canRequest()) {
                            // Pending: the other side has to decide first.
                            binding.btnSendRequest.setEnabled(false);
                            binding.btnSendRequest.setText(R.string.requests_awaiting_reply);
                            Toast.makeText(SendRequestActivity.this,
                                    getString(R.string.requests_awaiting_toast, targetUser.getName()),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(String message) {
                        // Leave the form usable; the server still guards the send.
                    }
                });
    }

    /**
     * A pair of members shares one exchange. If a request is already pending or
     * the exchange was accepted, the send is refused by the server; an accepted
     * exchange sends the user straight to the chat instead of back to a form.
     */
    private void send(String teach, String want) {
        String msg = binding.etMessage.getText().toString().trim();

        if (targetUser.getId() == null || targetUser.getId().isEmpty()) {
            Toast.makeText(this, "Cannot send request: partner account is not synced to the cloud database.", Toast.LENGTH_LONG).show();
            return;
        }

        binding.btnSendRequest.setEnabled(false);
        repository.sendExchangeRequest(targetUser.getId(), teach, want, msg,
                new SkillBarterRepository.DataCallback<ExchangeRequest>() {
                    @Override
                    public void onSuccess(ExchangeRequest data) {
                        if (isFinishing() || isDestroyed()) return;
                        Toast.makeText(SendRequestActivity.this, "Exchange Request Sent Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        if (isFinishing() || isDestroyed()) return;
                        binding.btnSendRequest.setEnabled(true);

                        // Already chatting: send them to the conversation instead.
                        if (message != null && message.toLowerCase().contains("already")) {
                            Toast.makeText(SendRequestActivity.this, message, Toast.LENGTH_LONG).show();
                            openChat(null);
                            return;
                        }

                        Toast.makeText(SendRequestActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Sends the user to the conversation with this partner. The request id is
     * carried through so the chat can later open the exchange details screen.
     */
    private void openChat(String existingRequestId) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("partnerUser", targetUser);
        if (existingRequestId != null) {
            intent.putExtra("requestId", existingRequestId);
        }
        startActivity(intent);
        finish();
    }
}
