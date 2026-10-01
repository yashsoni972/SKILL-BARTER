package com.yashsoni.skillbarter.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yashsoni.skillbarter.data.model.Conversation;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.FragmentChatListBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;

import java.util.List;

public class ChatListFragment extends Fragment {

    private FragmentChatListBinding binding;
    private SkillBarterRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = SkillBarterRepository.getInstance(requireContext());
        loadConversations();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadConversations();
    }

    /**
     * Lists only real conversations (accepted exchanges). This used to list every
     * registered user with a made-up last message, which made the Chats tab
     * useless and sent people into conversations that did not exist.
     */
    private void loadConversations() {
        if (binding == null) return;

        repository.fetchConversations(new SkillBarterRepository.DataCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (binding == null) return;
                if (conversations == null || conversations.isEmpty()) {
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setText("No chats yet.\n\nAccept a skill exchange request and the conversation will appear here.");
                    binding.rvChatList.setAdapter(null);
                    return;
                }
                binding.tvEmpty.setVisibility(View.GONE);
                ChatAdapter adapter = new ChatAdapter(conversations, conversation -> {
                    User partner = (User) conversation.getPartner();
                    if (partner == null || partner.getId() == null) {
                        return;
                    }
                    Intent intent = new Intent(requireContext(), ChatActivity.class);
                    intent.putExtra("partnerUser", partner);
                    intent.putExtra("requestId", conversation.getRequestId());
                    startActivity(intent);
                });
                binding.rvChatList.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.rvChatList.setAdapter(adapter);
            }

            @Override
            public void onError(String message) {
                if (binding == null) return;
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.tvEmpty.setText("Could not load chats:\n" + message);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
