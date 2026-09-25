package com.yashsoni.skillbarter.ui.discover;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.FragmentDiscoverBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.home.UserAdapter;
import com.yashsoni.skillbarter.ui.requests.SendRequestActivity;

import java.util.List;

public class DiscoverFragment extends Fragment {

    private FragmentDiscoverBinding binding;
    private SkillBarterRepository repository;
    private String selectedCategory = "";

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

        loadUsers("");

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
            filterSearch();
        });
    }

    private void filterSearch() {
        String query = binding.etSearch.getText().toString();
        if (!selectedCategory.isEmpty()) {
            query = query.isEmpty() ? selectedCategory : query + " " + selectedCategory;
        }
        loadUsers(query);
    }

    private void loadUsers(String query) {
        List<User> results = repository.searchUsers(query);
        if (results.isEmpty()) {
            binding.tvResultsCount.setText("No matching skill partners found yet.");
        } else {
            binding.tvResultsCount.setText("Found " + results.size() + " matching partner" + (results.size() > 1 ? "s" : ""));
        }

        UserAdapter adapter = new UserAdapter(results, user -> {
            Intent intent = new Intent(requireContext(), SendRequestActivity.class);
            intent.putExtra("targetUser", user);
            startActivity(intent);
        });

        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSearchResults.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
