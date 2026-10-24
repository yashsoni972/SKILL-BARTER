package com.yashsoni.skillbarter.ui.roadmap;

import android.graphics.Paint;
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_roadmap_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoadmapStep step = steps.get(position);

        holder.tvStepNumber.setText(String.valueOf(position + 1));
        holder.tvTitle.setText(step.getTitle());
        holder.tvDesc.setText(step.getDescription());

        // Resources section
        List<String> resources = step.getResources();
        if (resources != null && !resources.isEmpty()) {
            holder.layoutResources.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            for (String res : resources) {
                sb.append("• ").append(res).append("\n");
            }
            // Remove trailing newline
            if (sb.length() > 0) sb.setLength(sb.length() - 1);
            holder.tvResources.setText(sb.toString());
        } else {
            holder.layoutResources.setVisibility(View.GONE);
        }

        applyCompletedStyle(holder, step.isCompleted());

        holder.cbStep.setOnCheckedChangeListener(null);
        holder.cbStep.setChecked(step.isCompleted());
        holder.cbStep.setOnCheckedChangeListener((btn, isChecked) -> {
            step.setCompleted(isChecked);
            applyCompletedStyle(holder, isChecked);
            if (onProgressChanged != null) onProgressChanged.run();
        });
    }

    private void applyCompletedStyle(ViewHolder holder, boolean completed) {
        if (completed) {
            holder.viewStepBg.setBackgroundResource(R.drawable.bg_step_number_done);
            holder.tvStepNumber.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTitle.setAlpha(0.5f);
            holder.tvDesc.setAlpha(0.4f);
        } else {
            holder.viewStepBg.setBackgroundResource(R.drawable.bg_step_number);
            holder.tvStepNumber.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTitle.setAlpha(1f);
            holder.tvDesc.setAlpha(1f);
        }
    }

    @Override
    public int getItemCount() {
        return steps != null ? steps.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View viewStepBg;
        TextView tvStepNumber, tvTitle, tvDesc, tvResources;
        LinearLayout layoutResources;
        CheckBox cbStep;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewStepBg      = itemView.findViewById(R.id.viewStepBg);
            tvStepNumber    = itemView.findViewById(R.id.tvStepNumber);
            tvTitle         = itemView.findViewById(R.id.tvStepTitle);
            tvDesc          = itemView.findViewById(R.id.tvStepDesc);
            layoutResources = itemView.findViewById(R.id.layoutResources);
            tvResources     = itemView.findViewById(R.id.tvResources);
            cbStep          = itemView.findViewById(R.id.cbStep);
        }
    }
}
