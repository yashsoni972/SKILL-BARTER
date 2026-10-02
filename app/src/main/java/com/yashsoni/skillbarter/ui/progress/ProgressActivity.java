package com.yashsoni.skillbarter.ui.progress;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.Progress;
import com.yashsoni.skillbarter.databinding.ActivityProgressBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;

import com.yashsoni.skillbarter.utils.SystemBars;
import java.util.List;

public class ProgressActivity extends AppCompatActivity {

    private ActivityProgressBinding binding;
    private SkillBarterRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProgressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        binding.ivBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProgress();
    }

    /**
     * Every number on this screen comes from the database. It used to be
     * hardcoded in the layout file (a fixed "18 Hours" / "React 70%"), which is
     * why the numbers never matched reality.
     */
    private void loadProgress() {
        repository.fetchProgress(new SkillBarterRepository.DataCallback<Progress>() {
            @Override
            public void onSuccess(Progress progress) {
                binding.tvTaughtHours.setText(formatHours(progress.getTaughtHours()));
                binding.tvLearnedHours.setText(formatHours(progress.getLearnedHours()));
                binding.tvSessionsTaught.setText(String.valueOf(progress.getSessionsTaught()));
                binding.tvSessionsLearned.setText(String.valueOf(progress.getSessionsLearned()));

                renderSkills(progress.getSkills());
            }

            @Override
            public void onError(String message) {
                binding.tvTaughtHours.setText("0");
                binding.tvLearnedHours.setText("0");
                binding.tvSessionsTaught.setText("0");
                binding.tvSessionsLearned.setText("0");
                binding.tvEmptySkills.setVisibility(View.VISIBLE);
                binding.tvEmptySkills.setText("Could not load progress: " + message);
            }
        });
    }

    private void renderSkills(List<Progress.SkillProgress> skills) {
        binding.layoutSkills.removeAllViews();

        if (skills == null || skills.isEmpty()) {
            binding.tvEmptySkills.setVisibility(View.VISIBLE);
            return;
        }
        binding.tvEmptySkills.setVisibility(View.GONE);

        for (Progress.SkillProgress skill : skills) {
            View row = getLayoutInflater().inflate(R.layout.item_skill_progress, binding.layoutSkills, false);

            TextView name = row.findViewById(R.id.tvSkillName);
            TextView detail = row.findViewById(R.id.tvSkillDetail);
            ProgressBar bar = row.findViewById(R.id.progressSkill);

            name.setText(skill.getName());
            // "2 of 4 sessions (50%)" explains exactly what the bar means.
            detail.setText(skill.getCompleted() + " of " + skill.getTarget() + " sessions (" + skill.getPercent() + "%)");
            bar.setProgress(skill.getPercent());

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 8;
            binding.layoutSkills.addView(row, lp);
        }
    }

    /** Shows "1.5 hrs" / "2 hrs" / "0 hrs" - never a bare confusing number. */
    private String formatHours(double hours) {
        if (hours <= 0) return "0 hrs";
        if (hours == Math.floor(hours)) {
            return ((int) hours) + " hrs";
        }
        return String.format(java.util.Locale.getDefault(), "%.1f hrs", hours);
    }
}
