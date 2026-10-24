package com.yashsoni.skillbarter.ui.roadmap;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yashsoni.skillbarter.data.model.LearningRoadmap;
import com.yashsoni.skillbarter.data.model.RoadmapStep;
import com.yashsoni.skillbarter.databinding.ActivityRoadmapGeneratorBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.utils.SystemBars;

import java.util.List;

public class RoadmapGeneratorActivity extends AppCompatActivity {

    private ActivityRoadmapGeneratorBinding binding;
    private SkillBarterRepository repository;
    private RoadmapStepAdapter adapter;
    private LearningRoadmap currentRoadmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoadmapGeneratorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        binding.ivBack.setOnClickListener(v -> finish());

        String[] experiences = {"Beginner", "Intermediate", "Advanced"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, experiences);
        binding.spinnerExperience.setAdapter(spinnerAdapter);

        binding.btnGenerate.setOnClickListener(v -> {
            String current = binding.etCurrentSkill.getText().toString().trim();
            String goal    = binding.etGoalSkill.getText().toString().trim();
            String exp     = binding.spinnerExperience.getSelectedItem().toString();

            if (current.isEmpty() || goal.isEmpty()) {
                Toast.makeText(this,
                        "Please enter both your current skill and goal",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            generateRoadmap(current, goal, exp);
        });

        binding.rvSteps.setLayoutManager(new LinearLayoutManager(this));
    }

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
                        displayRoadmap(roadmap);
                    }

                    @Override
                    public void onError(String message) {
                        if (isFinishing() || isDestroyed() || binding == null) return;
                        binding.layoutLoading.setVisibility(View.GONE);
                        binding.btnGenerate.setEnabled(true);
                        Toast.makeText(RoadmapGeneratorActivity.this,
                                message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void displayRoadmap(LearningRoadmap roadmap) {
        binding.layoutResult.setVisibility(View.VISIBLE);
        binding.tvRoadmapTitle.setText(roadmap.getTitle());
        binding.tvRoadmapDesc.setText(roadmap.getDescription());

        adapter = new RoadmapStepAdapter(roadmap.getSteps(), this::updateProgress);
        binding.rvSteps.setAdapter(adapter);
        updateProgress();

        // Entrance animation for result section
        binding.layoutResult.setAlpha(0f);
        binding.layoutResult.setTranslationY(30f);
        binding.layoutResult.animate()
                .alpha(1f).translationY(0f)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void updateProgress() {
        if (currentRoadmap == null || currentRoadmap.getSteps() == null) return;
        List<RoadmapStep> steps = currentRoadmap.getSteps();
        int total = steps.size();
        int completed = 0;
        for (RoadmapStep step : steps) {
            if (step.isCompleted()) completed++;
        }

        binding.tvProgressLabel.setText(completed + " / " + total + " completed");
        int percent = total > 0 ? (completed * 100) / total : 0;
        binding.tvProgressPercent.setText(percent + "%");

        // Animate progress bar smoothly
        ObjectAnimator anim = ObjectAnimator.ofInt(
                binding.roadmapProgressBar, "progress",
                binding.roadmapProgressBar.getProgress(), percent);
        anim.setDuration(400);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
