package com.yashsoni.skillbarter.ui.roadmap;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yashsoni.skillbarter.data.model.LearningRoadmap;
import com.yashsoni.skillbarter.data.model.RoadmapStep;
import com.yashsoni.skillbarter.databinding.ActivityRoadmapGeneratorBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.utils.SystemBars;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RoadmapGeneratorActivity extends AppCompatActivity {

    private static final String PREFS_NAME    = "RoadmapProgress";
    private static final String KEY_ROADMAP   = "savedRoadmap";
    private static final long   STEP_DELAY_MS = 350; // delay between each step appearing

    private ActivityRoadmapGeneratorBinding binding;
    private SkillBarterRepository repository;
    private RoadmapStepAdapter adapter;
    private LearningRoadmap currentRoadmap;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoadmapGeneratorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        binding.ivBack.setOnClickListener(v -> finish());

        String[] experiences = {"Beginner", "Intermediate", "Advanced"};
        binding.spinnerExperience.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, experiences));

        binding.btnGenerate.setOnClickListener(v -> {
            String current = binding.etCurrentSkill.getText().toString().trim();
            String goal    = binding.etGoalSkill.getText().toString().trim();
            String exp     = binding.spinnerExperience.getSelectedItem().toString();
            if (current.isEmpty() || goal.isEmpty()) {
                Toast.makeText(this, "Please enter both your current skill and goal", Toast.LENGTH_SHORT).show();
                return;
            }
            generateRoadmap(current, goal, exp);
        });

        binding.btnShareRoadmap.setOnClickListener(v -> shareRoadmap());

        binding.rvSteps.setLayoutManager(new LinearLayoutManager(this));

        // Restore any previously saved roadmap
        restoreSavedRoadmap();
    }

    // ── Generate ──────────────────────────────────────────────────────────────

    private void generateRoadmap(String current, String goal, String exp) {
        binding.layoutLoading.setVisibility(View.VISIBLE);
        binding.layoutResult.setVisibility(View.GONE);
        binding.btnGenerate.setEnabled(false);

        repository.generateRoadmap(current, goal, exp,
                new SkillBarterRepository.DataCallback<LearningRoadmap>() {
                    @Override
                    public void onSuccess(LearningRoadmap roadmap) {
                        if (isFinishing() || isDestroyed() || binding == null) return;
                        binding.layoutLoading.setVisibility(View.GONE);
                        binding.btnGenerate.setEnabled(true);
                        currentRoadmap = roadmap;
                        displayRoadmapWithStreaming(roadmap);
                    }

                    @Override
                    public void onError(String message) {
                        if (isFinishing() || isDestroyed() || binding == null) return;
                        binding.layoutLoading.setVisibility(View.GONE);
                        binding.btnGenerate.setEnabled(true);
                        Toast.makeText(RoadmapGeneratorActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ── Display with live streaming animation ─────────────────────────────────

    /**
     * Shows the result header immediately, then reveals each step card one by
     * one with a staggered slide-up + fade-in animation — giving a "live
     * generation" feel without any actual streaming API needed.
     */
    private void displayRoadmapWithStreaming(LearningRoadmap roadmap) {
        binding.layoutResult.setVisibility(View.VISIBLE);
        binding.tvRoadmapTitle.setText(roadmap.getTitle());
        binding.tvRoadmapDesc.setText(roadmap.getDescription());
        binding.btnShareRoadmap.setVisibility(View.VISIBLE);

        // Fade the result card in
        binding.layoutResult.setAlpha(0f);
        binding.layoutResult.setTranslationY(20f);
        binding.layoutResult.animate().alpha(1f).translationY(0f)
                .setDuration(400).setInterpolator(new DecelerateInterpolator()).start();

        // Start with an empty list — steps will be added one by one
        List<RoadmapStep> allSteps = roadmap.getSteps();
        List<RoadmapStep> streamedSteps = new ArrayList<>();

        adapter = new RoadmapStepAdapter(streamedSteps, this::updateProgress);
        binding.rvSteps.setAdapter(adapter);

        // Cancel any pending step animations from a previous run
        handler.removeCallbacksAndMessages(null);

        for (int i = 0; i < allSteps.size(); i++) {
            final int index = i;
            final RoadmapStep step = allSteps.get(i);
            handler.postDelayed(() -> {
                if (isFinishing() || isDestroyed() || binding == null) return;
                streamedSteps.add(step);
                adapter.notifyItemInserted(streamedSteps.size() - 1);
                // Scroll to show the newly added card
                binding.rvSteps.smoothScrollToPosition(streamedSteps.size() - 1);
                updateProgress();
                // If last step, save to prefs
                if (index == allSteps.size() - 1) {
                    saveRoadmapToPrefs(roadmap);
                }
            }, STEP_DELAY_MS * (i + 1));
        }
    }

    // ── Progress bar ──────────────────────────────────────────────────────────

    private void updateProgress() {
        if (currentRoadmap == null || currentRoadmap.getSteps() == null) return;
        List<RoadmapStep> steps = currentRoadmap.getSteps();
        int total = steps.size(), completed = 0;
        for (RoadmapStep s : steps) if (s.isCompleted()) completed++;

        binding.tvProgressLabel.setText(completed + " / " + total + " completed");
        int percent = total > 0 ? (completed * 100) / total : 0;
        binding.tvProgressPercent.setText(percent + "%");

        ObjectAnimator anim = ObjectAnimator.ofInt(
                binding.roadmapProgressBar, "progress",
                binding.roadmapProgressBar.getProgress(), percent);
        anim.setDuration(500);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();

        // Save progress whenever a checkbox changes
        if (currentRoadmap != null) saveRoadmapToPrefs(currentRoadmap);
    }

    // ── Save / restore progress ───────────────────────────────────────────────

    private void saveRoadmapToPrefs(LearningRoadmap roadmap) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_ROADMAP, gson.toJson(roadmap)).apply();
    }

    private void restoreSavedRoadmap() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_ROADMAP, null);
        if (json == null) return;
        try {
            LearningRoadmap saved = gson.fromJson(json, LearningRoadmap.class);
            if (saved != null && saved.getSteps() != null && !saved.getSteps().isEmpty()) {
                currentRoadmap = saved;
                // Restore without animation (already seen before)
                binding.layoutResult.setVisibility(View.VISIBLE);
                binding.tvRoadmapTitle.setText(saved.getTitle());
                binding.tvRoadmapDesc.setText(saved.getDescription());
                binding.btnShareRoadmap.setVisibility(View.VISIBLE);

                adapter = new RoadmapStepAdapter(saved.getSteps(), this::updateProgress);
                binding.rvSteps.setAdapter(adapter);
                updateProgress();
            }
        } catch (Exception ignored) {}
    }

    // ── Share roadmap as text ─────────────────────────────────────────────────

    private void shareRoadmap() {
        if (currentRoadmap == null) return;
        List<RoadmapStep> steps = currentRoadmap.getSteps();
        if (steps == null || steps.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        sb.append("🗺️ *").append(currentRoadmap.getTitle()).append("*\n\n");
        sb.append(currentRoadmap.getDescription()).append("\n\n");

        for (int i = 0; i < steps.size(); i++) {
            RoadmapStep s = steps.get(i);
            sb.append(s.isCompleted() ? "✅" : "⬜")
              .append(" *Step ").append(i + 1).append(": ").append(s.getTitle()).append("*\n");
            if (s.getEstimatedTime() != null && !s.getEstimatedTime().isEmpty()) {
                sb.append("⏱ ").append(s.getEstimatedTime()).append("\n");
            }
            sb.append(s.getDescription()).append("\n");
            if (s.getMiniTask() != null && !s.getMiniTask().isEmpty()) {
                sb.append("🎯 Task: ").append(s.getMiniTask()).append("\n");
            }
            sb.append("\n");
        }

        // Progress summary
        int total = steps.size(), completed = 0;
        for (RoadmapStep s : steps) if (s.isCompleted()) completed++;
        sb.append("📊 Progress: ").append(completed).append("/").append(total)
          .append(" steps completed\n\n");
        sb.append("Generated with SkillBarter 🚀");

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(share, "Share Roadmap via"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }
}
