package com.yashsoni.skillbarter.ui.roadmap;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.RoadmapStep;

import java.util.List;

public class RoadmapStepAdapter extends RecyclerView.Adapter<RoadmapStepAdapter.ViewHolder> {

    private final List<RoadmapStep> steps;
    private final Runnable onProgressChanged;

    public RoadmapStepAdapter(List<RoadmapStep> steps, Runnable onProgressChanged) {
        this.steps = steps;
        this.onProgressChanged = onProgressChanged;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_roadmap_step, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        RoadmapStep step = steps.get(position);

        h.tvStepNumber.setText(String.valueOf(position + 1));
        h.tvTitle.setText(step.getTitle());
        h.tvDesc.setText(step.getDescription());

        // ── Estimated time badge ──────────────────────────────────────────────
        if (step.getEstimatedTime() != null && !step.getEstimatedTime().isEmpty()) {
            h.tvEstimatedTime.setVisibility(View.VISIBLE);
            h.tvEstimatedTime.setText("⏱ " + step.getEstimatedTime());
        } else {
            h.tvEstimatedTime.setVisibility(View.GONE);
        }

        // ── Mini task ─────────────────────────────────────────────────────────
        if (step.getMiniTask() != null && !step.getMiniTask().isEmpty()) {
            h.layoutMiniTask.setVisibility(View.VISIBLE);
            h.tvMiniTask.setText(step.getMiniTask());
        } else {
            h.layoutMiniTask.setVisibility(View.GONE);
        }

        // ── Resources ─────────────────────────────────────────────────────────
        List<String> resources = step.getResources();
        if (resources != null && !resources.isEmpty()) {
            h.layoutResources.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            for (String res : resources) {
                sb.append("• ").append(res).append("\n");
            }
            if (sb.length() > 0) sb.setLength(sb.length() - 1);
            h.tvResources.setText(sb.toString());
        } else {
            h.layoutResources.setVisibility(View.GONE);
        }

        // ── YouTube button ────────────────────────────────────────────────────
        String ytQuery = step.getYoutubeSearch();
        if (ytQuery != null && !ytQuery.isEmpty()) {
            h.btnYoutube.setVisibility(View.VISIBLE);
            h.btnYoutube.setOnClickListener(v -> {
                String url = "https://www.youtube.com/results?search_query="
                        + Uri.encode(ytQuery);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                v.getContext().startActivity(intent);
            });
        } else {
            h.btnYoutube.setVisibility(View.GONE);
        }

        // ── Completed state ───────────────────────────────────────────────────
        applyCompletedStyle(h, step.isCompleted());

        h.cbStep.setOnCheckedChangeListener(null);
        h.cbStep.setChecked(step.isCompleted());
        h.cbStep.setOnCheckedChangeListener((btn, isChecked) -> {
            step.setCompleted(isChecked);
            applyCompletedStyle(h, isChecked);
            if (onProgressChanged != null) onProgressChanged.run();
        });
    }

    private void applyCompletedStyle(ViewHolder h, boolean done) {
        if (done) {
            h.viewStepBg.setBackgroundResource(R.drawable.bg_step_number_done);
            h.tvStepNumber.setTextColor(
                    ContextCompat.getColor(h.itemView.getContext(), R.color.white));
            h.tvTitle.setPaintFlags(h.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            h.tvTitle.setAlpha(0.5f);
            h.tvDesc.setAlpha(0.4f);
            h.layoutMiniTask.setAlpha(0.5f);
        } else {
            h.viewStepBg.setBackgroundResource(R.drawable.bg_step_number);
            h.tvStepNumber.setTextColor(
                    ContextCompat.getColor(h.itemView.getContext(), R.color.primary));
            h.tvTitle.setPaintFlags(h.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            h.tvTitle.setAlpha(1f);
            h.tvDesc.setAlpha(1f);
            h.layoutMiniTask.setAlpha(1f);
        }
    }

    @Override
    public int getItemCount() {
        return steps != null ? steps.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View       viewStepBg;
        TextView   tvStepNumber, tvTitle, tvDesc, tvEstimatedTime, tvMiniTask, tvResources;
        LinearLayout layoutMiniTask, layoutResources, btnYoutube;
        CheckBox   cbStep;

        ViewHolder(@NonNull View item) {
            super(item);
            viewStepBg      = item.findViewById(R.id.viewStepBg);
            tvStepNumber    = item.findViewById(R.id.tvStepNumber);
            tvTitle         = item.findViewById(R.id.tvStepTitle);
            tvDesc          = item.findViewById(R.id.tvStepDesc);
            tvEstimatedTime = item.findViewById(R.id.tvEstimatedTime);
            layoutMiniTask  = item.findViewById(R.id.layoutMiniTask);
            tvMiniTask      = item.findViewById(R.id.tvMiniTask);
            layoutResources = item.findViewById(R.id.layoutResources);
            tvResources     = item.findViewById(R.id.tvResources);
            btnYoutube      = item.findViewById(R.id.btnWatchOnYoutube);
            cbStep          = item.findViewById(R.id.cbStep);
        }
    }
}
