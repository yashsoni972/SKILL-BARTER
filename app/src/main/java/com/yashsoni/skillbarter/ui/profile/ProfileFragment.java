package com.yashsoni.skillbarter.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.Availability;
import com.yashsoni.skillbarter.data.model.Badge;
import com.yashsoni.skillbarter.data.model.Credit;
import com.yashsoni.skillbarter.data.model.ExchangeRequest;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.FragmentProfileBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.auth.LoginActivity;
import com.yashsoni.skillbarter.ui.availability.AvailabilityActivity;
import com.yashsoni.skillbarter.ui.badges.BadgeAdapter;
import com.yashsoni.skillbarter.ui.credits.CreditsActivity;
import com.yashsoni.skillbarter.ui.exchanges.ExchangesActivity;
import com.yashsoni.skillbarter.ui.progress.ProgressActivity;
import com.yashsoni.skillbarter.ui.skills.AddSkillsActivity;
import com.yashsoni.skillbarter.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;
    private SkillBarterRepository repository;
    private int incomingRequestCount;
    private int outgoingRequestCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        repository = SkillBarterRepository.getInstance(requireContext());

        loadUserData();

        binding.ivAvatar.setOnClickListener(v -> showAvatarPickerDialog());

        binding.btnCredits.setOnClickListener(v -> startActivity(new Intent(requireContext(), CreditsActivity.class)));
        binding.btnProgress.setOnClickListener(v -> startActivity(new Intent(requireContext(), ProgressActivity.class)));
        binding.btnMyExchanges.setOnClickListener(v -> startActivity(new Intent(requireContext(), ExchangesActivity.class)));

        binding.btnEditOffered.setOnClickListener(v -> openAddSkills(0));
        binding.btnEditWanted.setOnClickListener(v -> openAddSkills(1));
        binding.btnManageAvailability.setOnClickListener(v -> startActivity(new Intent(requireContext(), AvailabilityActivity.class)));

        binding.btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
        loadBadges();
        loadRequestCount();
        loadAvailabilitySummary();
        loadCreditBalance();
    }

    /** Shows the real balance on the Credits button instead of a fixed label. */
    private void loadCreditBalance() {
        repository.fetchCredits(new SkillBarterRepository.DataCallback<Credit>() {
            @Override
            public void onSuccess(Credit credit) {
                if (binding == null) return;
                binding.btnCredits.setText(getString(R.string.credits_balance_format, credit.getBalance()));
            }

            @Override
            public void onError(String message) {
                // Keep the plain "Credits" label rather than showing an error.
            }
        });
    }

    private void showAvatarPickerDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_avatar_picker, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        ImageView iv1 = dialogView.findViewById(R.id.ivAvatar1);
        ImageView iv2 = dialogView.findViewById(R.id.ivAvatar2);
        ImageView iv3 = dialogView.findViewById(R.id.ivAvatar3);
        ImageView iv4 = dialogView.findViewById(R.id.ivAvatar4);
        ImageView iv5 = dialogView.findViewById(R.id.ivAvatar5);
        ImageView iv6 = dialogView.findViewById(R.id.ivAvatar6);

        View.OnClickListener avatarClickListener = v -> {
            String selectedAvatarKey = "avatar_1";
            int id = v.getId();
            if (id == R.id.ivAvatar1) selectedAvatarKey = "avatar_1";
            else if (id == R.id.ivAvatar2) selectedAvatarKey = "avatar_2";
            else if (id == R.id.ivAvatar3) selectedAvatarKey = "avatar_3";
            else if (id == R.id.ivAvatar4) selectedAvatarKey = "avatar_4";
            else if (id == R.id.ivAvatar5) selectedAvatarKey = "avatar_5";
            else if (id == R.id.ivAvatar6) selectedAvatarKey = "avatar_6";

            User user = sessionManager.getUser();
            if (user != null) {
                user.setProfileImage(selectedAvatarKey);
                sessionManager.updateUser(user);
                loadUserData();
                loadBadges();
                Toast.makeText(requireContext(), "Profile avatar updated!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        };

        iv1.setOnClickListener(avatarClickListener);
        iv2.setOnClickListener(avatarClickListener);
        iv3.setOnClickListener(avatarClickListener);
        iv4.setOnClickListener(avatarClickListener);
        iv5.setOnClickListener(avatarClickListener);
        iv6.setOnClickListener(avatarClickListener);

        dialog.show();
    }

    private void loadUserData() {
        User user = sessionManager.getUser();
        if (user != null) {
            binding.tvUserName.setText(user.getName());
            binding.tvUserEmail.setText(user.getEmail());
            binding.tvUserLocation.setText("📍 " + user.getLocation());
            binding.tvUserBio.setText("“" + user.getBio() + "”");

            if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                binding.ivAvatar.setImageResource(getAvatarResource(user.getProfileImage()));
            }

            int offeredCount = (user.getOfferedSkills() != null) ? user.getOfferedSkills().size() : 0;
            int wantedCount = (user.getWantedSkills() != null) ? user.getWantedSkills().size() : 0;
            int totalSkills = offeredCount + wantedCount;

            int requestCount = incomingRequestCount + outgoingRequestCount;
            int totalCompleted = user.getTotalExchanges();

            binding.tvSkillsStat.setText(String.valueOf(totalSkills));
            binding.tvRequestsStat.setText(String.valueOf(requestCount));
            binding.tvCompletedStat.setText(String.valueOf(totalCompleted));

            // Badges are loaded asynchronously from the backend

            binding.chipGroupOffered.removeAllViews();
            List<String> offered = user.getOfferedSkills();
            if (offered != null) {
                for (String s : offered) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(s);
                    binding.chipGroupOffered.addView(chip);
                }
            }

            binding.chipGroupWanted.removeAllViews();
            List<String> wanted = user.getWantedSkills();
            if (wanted != null) {
                for (String s : wanted) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(s);
                    binding.chipGroupWanted.addView(chip);
                }
            }
        }
    }

    private void loadRequestCount() {
        repository.fetchIncomingRequests(new SkillBarterRepository.DataCallback<List<ExchangeRequest>>() {
            @Override
            public void onSuccess(List<ExchangeRequest> data) {
                incomingRequestCount = data.size();
                applyRequestCount();
            }

            @Override
            public void onError(String message) {}
        });

        repository.fetchOutgoingRequests(new SkillBarterRepository.DataCallback<List<ExchangeRequest>>() {
            @Override
            public void onSuccess(List<ExchangeRequest> data) {
                outgoingRequestCount = data.size();
                applyRequestCount();
            }

            @Override
            public void onError(String message) {}
        });
    }

    private void loadBadges() {
        repository.fetchBadges(new SkillBarterRepository.DataCallback<List<Badge>>() {
            @Override
            public void onSuccess(List<Badge> badges) {
                if (binding == null) return;
                if (badges.isEmpty()) {
                    binding.rvBadges.setVisibility(View.GONE);
                    return;
                }
                binding.rvBadges.setVisibility(View.VISIBLE);
                binding.rvBadges.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
                binding.rvBadges.setAdapter(new BadgeAdapter(badges));
            }

            @Override
            public void onError(String message) {
                if (binding == null) return;
                binding.rvBadges.setVisibility(View.GONE);
            }
        });
    }

    /** Shows the real saved slots instead of a static placeholder string. */
    private void loadAvailabilitySummary() {
        repository.fetchMyAvailability(new SkillBarterRepository.DataCallback<List<Availability>>() {
            @Override
            public void onSuccess(List<Availability> slots) {
                if (binding == null) return;
                if (slots == null || slots.isEmpty()) {
                    binding.tvAvailabilitySummary.setText("No availability slots added yet");
                    return;
                }
                List<String> parts = new ArrayList<>();
                for (Availability slot : slots) {
                    parts.add(slot.getDay().substring(0, 3) + " " + slot.getStartTime() + "-" + slot.getEndTime());
                }
                binding.tvAvailabilitySummary.setText("• " + String.join("\n• ", parts));
            }

            @Override
            public void onError(String message) {
                if (binding == null) return;
                binding.tvAvailabilitySummary.setText("Could not load availability");
            }
        });
    }

    private void applyRequestCount() {
        if (binding == null) return;
        binding.tvRequestsStat.setText(String.valueOf(incomingRequestCount + outgoingRequestCount));
    }

    public static int getAvatarResource(String key) {
        if ("avatar_1".equals(key)) return R.drawable.avatar_1;
        if ("avatar_2".equals(key)) return R.drawable.avatar_2;
        if ("avatar_3".equals(key)) return R.drawable.avatar_3;
        if ("avatar_4".equals(key)) return R.drawable.avatar_4;
        if ("avatar_5".equals(key)) return R.drawable.avatar_5;
        if ("avatar_6".equals(key)) return R.drawable.avatar_6;
        return R.drawable.ic_profile;
    }

    private void openAddSkills(int selectedTab) {
        Intent intent = new Intent(requireContext(), AddSkillsActivity.class);
        intent.putExtra("selectedTab", selectedTab);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
