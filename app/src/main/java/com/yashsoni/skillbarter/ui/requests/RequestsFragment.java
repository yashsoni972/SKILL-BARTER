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
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.FragmentRequestsBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.chat.ChatActivity;
import com.yashsoni.skillbarter.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment {

    private FragmentRequestsBinding binding;
    private SkillBarterRepository repository;
    private SessionManager sessionManager;

    private final List<ExchangeRequest> incomingRequests = new ArrayList<>();
    private final List<ExchangeRequest> outgoingRequests = new ArrayList<>();

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
        sessionManager = new SessionManager(requireContext());

        updateTabTitles();
        submitList();

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                submitList();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) {
            refreshRequests();
        }
    }

    private boolean isIncomingSelected() {
        return binding == null || binding.tabLayout.getSelectedTabPosition() == 0;
    }

    private void refreshRequests() {
        if (binding == null) return;
        binding.rvRequests.setAdapter(null);

        repository.fetchIncomingRequests(new SkillBarterRepository.DataCallback<List<ExchangeRequest>>() {
            @Override
            public void onSuccess(List<ExchangeRequest> data) {
                if (binding == null) return;
                incomingRequests.clear();
                incomingRequests.addAll(data);
                updateTabTitles();
                submitList();
            }

            @Override
            public void onError(String message) {
                showError(message);
            }
        });

        repository.fetchOutgoingRequests(new SkillBarterRepository.DataCallback<List<ExchangeRequest>>() {
            @Override
            public void onSuccess(List<ExchangeRequest> data) {
                if (binding == null) return;
                outgoingRequests.clear();
                outgoingRequests.addAll(data);
                updateTabTitles();
                submitList();
            }

            @Override
            public void onError(String message) {
                showError(message);
            }
        });
    }

    private void updateTabTitles() {
        if (binding == null) return;

        if (binding.tabLayout.getTabAt(0) != null) {
            binding.tabLayout.getTabAt(0).setText("Received (" + incomingRequests.size() + ")");
        }
        if (binding.tabLayout.getTabAt(1) != null) {
            binding.tabLayout.getTabAt(1).setText("Sent (" + outgoingRequests.size() + ")");
        }
    }

    private void submitList() {
        if (binding == null) return;

        List<ExchangeRequest> list = isIncomingSelected() ? incomingRequests : outgoingRequests;

        User currentUser = sessionManager.getUser();
        String currentUserId = currentUser != null ? currentUser.getId() : null;
        final boolean incomingTab = isIncomingSelected();

        RequestAdapter adapter = new RequestAdapter(list, incomingTab, currentUserId, new RequestAdapter.OnRequestActionListener() {
            @Override
            public void onAccept(ExchangeRequest request) {
                repository.acceptRequestApi(request.getId(), new SkillBarterRepository.DataCallback<ExchangeRequest>() {
                    @Override
                    public void onSuccess(ExchangeRequest data) {
                        if (!isAdded()) return;

                        // Accept applies to incoming requests only; open the chat
                        // with the other person, not with the sender.
                        User other = request.getOtherUser(
                                sessionManager.getUser() != null ? sessionManager.getUser().getId() : null);
                        if (other == null || other.getId() == null) {
                            Toast.makeText(requireContext(), "Accepted, but this user has no id to chat with.", Toast.LENGTH_LONG).show();
                            refreshRequests();
                            return;
                        }

                        Toast.makeText(requireContext(), "Exchange Accepted! Opening Chat...", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(requireContext(), ChatActivity.class);
                        intent.putExtra("partnerUser", other);
                        // Carried through so a completed session can be tied to this
                        // exchange; without it the backend has no request to mark completed.
                        intent.putExtra("requestId", request.getId());
                        intent.putExtra("requestedSkill", request.getRequestedSkill());
                        intent.putExtra("offeredSkill", request.getOfferedSkill());
                        intent.putExtra("direction", request.getDirection());
                        startActivity(intent);
                        refreshRequests();
                    }

                    @Override
                    public void onError(String message) {
                        showError(message);
                    }
                });
            }

            @Override
            public void onReject(ExchangeRequest request) {
                repository.rejectRequestApi(request.getId(), new SkillBarterRepository.DataCallback<ExchangeRequest>() {
                    @Override
                    public void onSuccess(ExchangeRequest data) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Exchange Request Rejected", Toast.LENGTH_SHORT).show();
                        refreshRequests();
                    }

                    @Override
                    public void onError(String message) {
                        showError(message);
                    }
                });
            }

            @Override
            public void onChat(ExchangeRequest request, User partner) {
                if (partner == null || partner.getId() == null) {
                    showError("Cannot open chat: this user has no account id.");
                    return;
                }
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("partnerUser", partner);
                intent.putExtra("requestId", request.getId());
                intent.putExtra("requestedSkill", request.getRequestedSkill());
                intent.putExtra("offeredSkill", request.getOfferedSkill());
                intent.putExtra("direction", request.getDirection());
                startActivity(intent);
            }
        });

        binding.rvRequests.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRequests.setAdapter(adapter);
    }

    private void showError(String message) {
        if (!isAdded()) return;
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
