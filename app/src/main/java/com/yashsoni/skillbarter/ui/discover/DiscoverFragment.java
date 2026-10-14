package com.yashsoni.skillbarter.ui.discover;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.MatchResult;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.FragmentDiscoverBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.requests.SendRequestActivity;

import java.util.List;

public class DiscoverFragment extends Fragment {

    private FragmentDiscoverBinding binding;
    private SkillBarterRepository repository;
    private String selectedCategory = "";

    // Debounce: wait 500ms after last keystroke before calling API
    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debounceRunnable;
    private static final int DEBOUNCE_MS = 500;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = SkillBarterRepository.getInstance(requireContext());
        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initial load
        triggerSearch();

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel previous pending search
                if (debounceRunnable != null) debounceHandler.removeCallbacks(debounceRunnable);
                debounceRunnable = () -> triggerSearch();
                debounceHandler.postDelayed(debounceRunnable, DEBOUNCE_MS);
            }
        });

        binding.chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty() || checkedIds.contains(R.id.chipAll)) {
                selectedCategory = "";
            } else if (checkedIds.contains(R.id.chipProgramming)) {
                selectedCategory = "Programming";
            } else if (checkedIds.contains(R.id.chipDesign)) {
                selectedCategory = "Design";
            } else if (checkedIds.contains(R.id.chipLanguage)) {
                selectedCategory = "Language";
            } else if (checkedIds.contains(R.id.chipMusic)) {
                selectedCategory = "Music";
            }
            triggerSearch();
        });
    }

    private void triggerSearch() {
        if (binding == null) return;
        showLoading(true);

        String query = binding.etSearch.getText().toString().trim();

        repository.fetchDiscoverMatches(query, selectedCategory, "", new SkillBarterRepository.DataCallback<List<MatchResult>>() {
            @Override
            public void onSuccess(List<MatchResult> matches) {
                if (binding == null) return;
                showLoading(false);

                if (matches == null || matches.isEmpty()) {
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setText("No skill partners found.\nTry a different search.");
                    binding.tvResultsCount.setText("");
                    binding.rvSearchResults.setAdapter(null);
                    return;
                }

                binding.tvEmpty.setVisibility(View.GONE);
                binding.tvResultsCount.setText(matches.size() + " partners found");

                MatchAdapter adapter = new MatchAdapter(matches, new MatchAdapter.OnMatchActionListener() {
                    @Override
                    public void onViewProfile(User user) {
                        openSendRequest(user);
                    }

                    @Override
                    public void onExchange(User user) {
                        openSendRequest(user);
                    }
                });

                binding.rvSearchResults.setAdapter(adapter);

                // Fade in animation
                AlphaAnimation fade = new AlphaAnimation(0f, 1f);
                fade.setDuration(200);
                binding.rvSearchResults.startAnimation(fade);
            }

            @Override
            public void onError(String message) {
                if (binding == null) return;
                showLoading(false);
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.tvEmpty.setText("Could not load results.\nCheck your connection.");
                binding.tvResultsCount.setText("");
                binding.rvSearchResults.setAdapter(null);
            }
        });
    }

    private void showLoading(boolean show) {
        if (binding == null) return;
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            binding.tvEmpty.setVisibility(View.GONE);
        }
    }

    private void openSendRequest(User user) {
        Intent intent = new Intent(requireContext(), SendRequestActivity.class);
        intent.putExtra("targetUser", user);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (debounceRunnable != null) debounceHandler.removeCallbacks(debounceRunnable);
        binding = null;
    }
}
