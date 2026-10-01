package com.yashsoni.skillbarter.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yashsoni.skillbarter.MainActivity;
import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.Stats;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.FragmentHomeBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.helprequests.HelpRequestActivity;
import com.yashsoni.skillbarter.ui.marketplace.MarketplaceActivity;
import com.yashsoni.skillbarter.ui.notifications.NotificationsActivity;
import com.yashsoni.skillbarter.ui.requests.SendRequestActivity;
import com.yashsoni.skillbarter.utils.SessionManager;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SkillBarterRepository repository;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = SkillBarterRepository.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        loadHomeData();
        loadRecommendedPartners();

        binding.ivNotification.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), NotificationsActivity.class));
        });

        binding.tvSeeAll.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), MarketplaceActivity.class));
        });

        binding.etSearchTrigger.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectBottomTab(R.id.nav_search);
            }
        });

        binding.tvSeeAll.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectBottomTab(R.id.nav_search);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHomeData();
        loadRecommendedPartners();
        loadNotificationBadge();
    }

    /** Red dot on the bell, driven by the real unread count. */
    private void loadNotificationBadge() {
        repository.fetchUnreadCount(new SkillBarterRepository.DataCallback<Integer>() {
            @Override
            public void onSuccess(Integer count) {
                if (binding == null) return;
                int unread = count == null ? 0 : count;
                binding.tvNotificationBadge.setVisibility(unread > 0 ? View.VISIBLE : View.GONE);
                if (unread > 0) {
                    // 99+ keeps the circle from stretching on a large backlog.
                    binding.tvNotificationBadge.setText(unread > 99 ? "99+" : String.valueOf(unread));
                }
            }

            @Override
            public void onError(String message) {
                // The bell still works, it just shows no badge.
                if (binding != null) {
                    binding.tvNotificationBadge.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadHomeData() {
        User currentUser = sessionManager.getUser();
        if (currentUser != null && currentUser.getName() != null && !currentUser.getName().isEmpty()) {
            binding.tvGreeting.setText("Hello, " + currentUser.getName().split(" ")[0] + " 👋");
        } else {
            binding.tvGreeting.setText("Hello, Skill Barterer 👋");
        }

        repository.fetchLiveStats(new SkillBarterRepository.DataCallback<Stats>() {
            @Override
            public void onSuccess(Stats stats) {
                if (binding == null) return;
                binding.tvUsersCount.setText(String.valueOf(stats.getTotalUsers()));
                binding.tvActiveCount.setText(String.valueOf(stats.getActiveExchanges()));
                binding.tvCompletedCount.setText(String.valueOf(stats.getCompletedExchanges()));
            }

            @Override
            public void onError(String message) {
                if (binding == null) return;
                binding.tvUsersCount.setText("0");
                binding.tvActiveCount.setText("0");
                binding.tvCompletedCount.setText("0");
            }
        });
    }

    private void loadRecommendedPartners() {
        repository.fetchRecommendedUsers(new SkillBarterRepository.DataCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                if (binding == null) return;
                if (users.isEmpty()) {
                    binding.rvRecommended.setAdapter(null);
                    return;
                }
                UserAdapter adapter = new UserAdapter(users, user -> {
                    Intent intent = new Intent(requireContext(), SendRequestActivity.class);
                    intent.putExtra("targetUser", user);
                    startActivity(intent);
                });
                binding.rvRecommended.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.rvRecommended.setAdapter(adapter);
            }

            @Override
            public void onError(String message) {
                if (binding == null) return;
                binding.rvRecommended.setAdapter(null);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
