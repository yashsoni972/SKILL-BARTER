package com.yashsoni.skillbarter.ui.roadmap;

import android.os.Bundle;
import android.view.View;
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

        String[] experiences = new String[]{"Beginner", "Intermediate", "Advanced"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, experiences);
        binding.spinnerExperience.setAdapter(spinnerAdapter);

        binding.btnGenerate.setOnClickListener(v -> {
            String currentSkill = binding.etCurrentSkill.getText().toString().trim();
            String goal = binding.etGoalSkill.getText().toString().trim();
            String experience = binding.spinnerExperience.getSelectedItem().toString();

            if (currentSkill.isEmpty() || goal.isEmpty()) {
                Toast.makeText(this, "Please enter both your current skill and goal", Toast.LENGTH_SHORT).show();
                return;
            }

            generateRoadmap(currentSkill, goal, experience);
        });

        binding.rvSteps.setLayoutManager(new LinearLayoutManager(this));
    }

    private void generateRoadmap(String currentSkill, String goal, String experience) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutResult.setVisibility(View.GONE);
        binding.btnGenerate.setEnabled(false);

        repository.generateRoadmap(currentSkill, goal, experience, new SkillBarterRepository.DataCallback<LearningRoadmap>() {
            @Override
            public void onSuccess(LearningRoadmap roadmap) {
                if (isFinishing() || isDestroyed() || binding == null) return;
                binding.progressBar.setVisibility(View.GONE);
                binding.btnGenerate.setEnabled(true);
                currentRoadmap = roadmap;
                displayRoadmap(roadmap);
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed() || binding == null) return;
                binding.progressBar.setVisibility(View.GONE);
                binding.btnGenerate.setEnabled(true);
                Toast.makeText(RoadmapGeneratorActivity.this, message, Toast.LENGTH_LONG).show();
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
    }

    private void updateProgress() {
        if (currentRoadmap == null || currentRoadmap.getSteps() == null) return;
        List<RoadmapStep> steps = currentRoadmap.getSteps();
        int total = steps.size();
        int completed = 0;
        for (RoadmapStep step : steps) {
            if (step.isCompleted()) completed++;
        }

        binding.tvProgressLabel.setText("Progress: " + completed + " / " + total + " completed");
        int percent = total > 0 ? (completed * 100) / total : 0;
        binding.roadmapProgressBar.setProgress(percent);
    }
}
