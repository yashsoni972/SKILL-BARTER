package com.yashsoni.skillbarter.ui.roadmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_roadmap_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoadmapStep step = steps.get(position);
        holder.tvTitle.setText(step.getTitle());
        holder.tvDesc.setText(step.getDescription());
        
        holder.cbStep.setOnCheckedChangeListener(null);
        holder.cbStep.setChecked(step.isCompleted());
        
        holder.cbStep.setOnCheckedChangeListener((buttonView, isChecked) -> {
            step.setCompleted(isChecked);
            if (onProgressChanged != null) {
                onProgressChanged.run();
            }
        });
    }

    @Override
    public int getItemCount() {
        return steps != null ? steps.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbStep;
        TextView tvTitle, tvDesc;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbStep = itemView.findViewById(R.id.cbStep);
            tvTitle = itemView.findViewById(R.id.tvStepTitle);
            tvDesc = itemView.findViewById(R.id.tvStepDesc);
        }
    }
}
