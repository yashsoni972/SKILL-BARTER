package com.yashsoni.skillbarter.ui.roadmap;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

        // Step number (1-based)
        holder.tvStepNumber.setText(String.valueOf(position + 1));

        // Completed state styling
        applyCompletedStyle(holder, step.isCompleted());

        // Detach listener before setting checked state to avoid loops
        holder.cbStep.setOnCheckedChangeListener(null);
        holder.cbStep.setChecked(step.isCompleted());

        holder.cbStep.setOnCheckedChangeListener((btn, isChecked) -> {
            step.setCompleted(isChecked);
            applyCompletedStyle(holder, isChecked);
            notifyItemChanged(holder.getAdapterPosition());
            if (onProgressChanged != null) onProgressChanged.run();
        });
    }

    private void applyCompletedStyle(ViewHolder holder, boolean completed) {
        if (completed) {
            // Green circle for done steps
            holder.viewStepBg.setBackgroundResource(R.drawable.bg_step_number_done);
            holder.tvStepNumber.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            // Strikethrough on title
            holder.tvTitle.setPaintFlags(
                    holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTitle.setAlpha(0.55f);
            holder.tvDesc.setAlpha(0.45f);
        } else {
            holder.viewStepBg.setBackgroundResource(R.drawable.bg_step_number);
            holder.tvStepNumber.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
            holder.tvTitle.setPaintFlags(
                    holder.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
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
        TextView tvStepNumber, tvTitle, tvDesc;
        CheckBox cbStep;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewStepBg   = itemView.findViewById(R.id.viewStepBg);
            tvStepNumber = itemView.findViewById(R.id.tvStepNumber);
            tvTitle      = itemView.findViewById(R.id.tvStepTitle);
            tvDesc       = itemView.findViewById(R.id.tvStepDesc);
            cbStep       = itemView.findViewById(R.id.cbStep);
        }
    }
}
