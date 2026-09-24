package com.yashsoni.skillbarter.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yashsoni.skillbarter.data.model.Message;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivityChatBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.exchange.ExchangeDetailsActivity;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private SkillBarterRepository repository;
    private User partnerUser;
    private MessageAdapter adapter;
    private List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        partnerUser = (User) getIntent().getSerializableExtra("partnerUser");
        if (partnerUser == null) {
            partnerUser = new User("u1", "Riya Sharma", "riya@example.com", "Ahmedabad", "UI/UX Designer", 4.8, 5);
        }

        binding.tvUserName.setText(partnerUser.getName());

        binding.ivBack.setOnClickListener(v -> finish());

        messageList = repository.getMessagesForPartner(partnerUser.getId());
        adapter = new MessageAdapter(messageList);

        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMessages.setAdapter(adapter);

        binding.btnSend.setOnClickListener(v -> {
            String text = binding.etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                repository.sendMessage(partnerUser.getId(), text);
                messageList.add(new Message("curr_user", partnerUser.getId(), text, "Just now"));
                adapter.notifyItemInserted(messageList.size() - 1);
                binding.rvMessages.scrollToPosition(messageList.size() - 1);
                binding.etMessage.setText("");
            }
        });

        binding.btnSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, ExchangeDetailsActivity.class);
            intent.putExtra("partnerUser", partnerUser);
            startActivity(intent);
        });
    }
}
