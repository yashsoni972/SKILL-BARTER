package com.yashsoni.skillbarter.ui.credits;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.Credit;
import com.yashsoni.skillbarter.databinding.ActivityCreditsBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;

import com.yashsoni.skillbarter.utils.SystemBars;
import java.util.List;

/**
 * Shows the member's real balance and ledger from GET /credits. The screen used
 * to render a literal "40 Credits" and three invented transactions straight from
 * the layout file, so it never reflected anything that actually happened.
 */
public class CreditsActivity extends AppCompatActivity {

    private ActivityCreditsBinding binding;
    private SkillBarterRepository repository;
    private CreditTransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreditsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        adapter = new CreditTransactionAdapter();
        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTransactions.setAdapter(adapter);

        binding.ivBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCredits();
    }

    private void loadCredits() {
        repository.fetchCredits(new SkillBarterRepository.DataCallback<Credit>() {
            @Override
            public void onSuccess(Credit credit) {
                if (isFinishing() || isDestroyed()) return;

                binding.tvBalance.setText(getString(R.string.credits_balance_format, credit.getBalance()));
                binding.tvTotals.setText(getString(R.string.credits_totals_format,
                        credit.getEarnedTotal(), credit.getSpentTotal()));

                List<com.yashsoni.skillbarter.data.model.CreditTransaction> history = credit.getHistory();
                boolean empty = history == null || history.isEmpty();
                binding.tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                binding.rvTransactions.setVisibility(empty ? View.GONE : View.VISIBLE);
                adapter.setTransactions(history);
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) return;
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.rvTransactions.setVisibility(View.GONE);
                binding.tvEmpty.setText(message);
                Toast.makeText(CreditsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}