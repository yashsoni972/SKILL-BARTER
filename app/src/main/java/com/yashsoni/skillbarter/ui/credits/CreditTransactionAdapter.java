package com.yashsoni.skillbarter.ui.credits;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.CreditTransaction;
import com.yashsoni.skillbarter.utils.TimeFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders the real credit ledger. Amount colour carries the sign, so a learner
 * sees the deduction in red and a teacher sees the earning in green.
 */
public class CreditTransactionAdapter extends RecyclerView.Adapter<CreditTransactionAdapter.ViewHolder> {

    private final List<CreditTransaction> transactions = new ArrayList<>();

    public void setTransactions(List<CreditTransaction> newTransactions) {
        transactions.clear();
        if (newTransactions != null) {
            transactions.addAll(newTransactions);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_credit_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CreditTransaction t = transactions.get(position);
        boolean earned = t.getAmount() >= 0;

        holder.tvDescription.setText(t.getDescription());
        holder.tvAmount.setText((earned ? "+" : "") + t.getAmount() + " credits");
        holder.tvAmount.setTextColor(holder.itemView.getContext().getColor(
                earned ? R.color.accent_green : R.color.primary));
        holder.tvDate.setText(TimeFormat.relative(t.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvDescription;
        final TextView tvAmount;
        final TextView tvDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}