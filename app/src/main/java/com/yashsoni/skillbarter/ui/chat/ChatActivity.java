package com.yashsoni.skillbarter.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yashsoni.skillbarter.data.model.Message;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivityChatBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.exchange.ExchangeDetailsActivity;
import com.yashsoni.skillbarter.ui.schedule.ScheduleSessionActivity;
import com.yashsoni.skillbarter.utils.SessionManager;
import com.yashsoni.skillbarter.utils.SystemBars;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final int REQUEST_SCHEDULE = 1001;

    private ActivityChatBinding binding;
    private SkillBarterRepository repository;
    private User partnerUser;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private String requestId;
    private String requestedSkill;
    private String offeredSkill;
    private String direction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        if (getIntent() != null && getIntent().hasExtra("partnerUser")) {
            partnerUser = (User) getIntent().getSerializableExtra("partnerUser");
            requestId = getIntent().getStringExtra("requestId");
            requestedSkill = getIntent().getStringExtra("requestedSkill");
            offeredSkill = getIntent().getStringExtra("offeredSkill");
            direction = getIntent().getStringExtra("direction");
        }
        if (partnerUser == null) {
            Toast.makeText(this, "No conversation partner selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // A user object without an id cannot address any API call, and letting
        // that reach Retrofit produces the confusing
        // 'Path parameter "userId" value must not be null' network error.
        if (partnerUser.getId() == null || partnerUser.getId().isEmpty()) {
            Toast.makeText(this, "This user has no account id. Please refresh and try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        binding.tvUserName.setText(partnerUser.getName());

        binding.ivBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        messageList = new ArrayList<>();
        User currentUser = new SessionManager(this).getUser();
        adapter = new MessageAdapter(messageList, currentUser != null ? currentUser.getId() : null);

        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMessages.setAdapter(adapter);

        loadMessages();

        binding.btnSend.setOnClickListener(v -> {
            String text = binding.etMessage.getText().toString().trim();
            if (text.isEmpty()) return;

            binding.btnSend.setEnabled(false);
            repository.sendMessageApi(partnerUser.getId(), text, new SkillBarterRepository.DataCallback<Message>() {
                @Override
                public void onSuccess(Message message) {
                    binding.btnSend.setEnabled(true);
                    binding.etMessage.setText("");
                    loadMessages();
                }

                @Override
                public void onError(String message) {
                    binding.btnSend.setEnabled(true);
                    Toast.makeText(ChatActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });

        // Agreeing a date and time is the point of this button, so it goes straight to
        // the scheduling screen. Exchange details stay reachable from there.
        binding.btnSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, ScheduleSessionActivity.class);
            intent.putExtra("requestId", requestId);
            intent.putExtra("partnerName", partnerUser.getName());
            intent.putExtra("skill", teachingOrLearning());
            startActivityForResult(intent, REQUEST_SCHEDULE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCHEDULE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Session scheduled", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * The skill this member is teaching in this exchange, falling back to the one
     * they asked for. Used as the subject line for the booked session.
     */
    private String teachingOrLearning() {
        boolean iAmSender = "outgoing".equals(direction);
        String taught = iAmSender ? offeredSkill : requestedSkill;
        String learned = iAmSender ? requestedSkill : offeredSkill;
        if (taught != null && !taught.trim().isEmpty()) return taught;
        if (learned != null && !learned.trim().isEmpty()) return learned;
        return "Skill exchange";
    }

    private void loadMessages() {
        repository.fetchMessages(partnerUser.getId(), new SkillBarterRepository.DataCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                messageList.clear();
                messageList.addAll(messages);
                adapter.notifyDataSetChanged();
                if (!messageList.isEmpty()) {
                    binding.rvMessages.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
