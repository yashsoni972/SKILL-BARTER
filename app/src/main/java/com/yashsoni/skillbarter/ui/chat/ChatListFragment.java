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

        List<User> activePartners = repository.getRecommendedUsers().subList(0, 2);

        ChatAdapter adapter = new ChatAdapter(activePartners, partner -> {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra("partnerUser", partner);
            startActivity(intent);
        });

        binding.rvChatList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChatList.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
