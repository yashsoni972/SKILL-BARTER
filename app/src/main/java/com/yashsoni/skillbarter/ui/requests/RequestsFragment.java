package com.yashsoni.skillbarter.ui.requests;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.yashsoni.skillbarter.data.model.ExchangeRequest;
import com.yashsoni.skillbarter.databinding.FragmentRequestsBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.chat.ChatActivity;

import java.util.List;

public class RequestsFragment extends Fragment {

    private FragmentRequestsBinding binding;
    private SkillBarterRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRequestsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = SkillBarterRepository.getInstance(requireContext());

        loadRequests(true);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadRequests(tab.getPosition() == 0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadRequests(boolean isIncoming) {
        List<ExchangeRequest> list = isIncoming ? repository.getIncomingRequests() : repository.getOutgoingRequests();

        RequestAdapter adapter = new RequestAdapter(list, new RequestAdapter.OnRequestActionListener() {
            @Override
            public void onAccept(ExchangeRequest request) {
                repository.acceptRequest(request.getId());
                Toast.makeText(requireContext(), "Exchange Accepted! Opening Chat...", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(requireContext(), ChatActivity.class);
                if (request.getSenderId() != null) {
                    intent.putExtra("partnerUser", request.getSenderId());
                }
                startActivity(intent);
                loadRequests(true);
            }

            @Override
            public void onReject(ExchangeRequest request) {
                repository.rejectRequest(request.getId());
                Toast.makeText(requireContext(), "Exchange Request Rejected", Toast.LENGTH_SHORT).show();
                loadRequests(true);
            }
        });

        binding.rvRequests.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRequests.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
