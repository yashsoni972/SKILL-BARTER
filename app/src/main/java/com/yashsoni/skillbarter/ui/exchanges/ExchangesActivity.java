package com.yashsoni.skillbarter.ui.exchanges;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yashsoni.skillbarter.data.model.ExchangeSummary;
import com.yashsoni.skillbarter.data.model.Review;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivityExchangesBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.chat.ChatActivity;
import com.yashsoni.skillbarter.ui.exchange.ExchangeDetailsActivity;
import com.yashsoni.skillbarter.ui.review.RateReviewActivity;

import com.yashsoni.skillbarter.utils.SystemBars;
import java.util.ArrayList;
import java.util.List;

/**
 * "Who did I exchange skills with" and "rate them". Backed by
 * GET /requests/exchanges, so every row is a real exchange rather than a sample.
 */
public class ExchangesActivity extends AppCompatActivity {

    private ActivityExchangesBinding binding;
    private SkillBarterRepository repository;
    private ExchangeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExchangesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        adapter = new ExchangeAdapter(new ExchangeAdapter.OnExchangeActionListener() {
            @Override
            public void onChat(ExchangeSummary exchange, User partner) {
                if (partner == null || partner.getId() == null) {
                    Toast.makeText(ExchangesActivity.this, "Cannot open chat: no account id.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(ExchangesActivity.this, ChatActivity.class);
                intent.putExtra("partnerUser", partner);
                intent.putExtra("requestId", exchange.getRequestId());
                intent.putExtra("requestedSkill", exchange.getRequestedSkill());
                intent.putExtra("offeredSkill", exchange.getOfferedSkill());
                intent.putExtra("direction", exchange.getDirection());
                startActivity(intent);
            }

            @Override
            public void onRate(ExchangeSummary exchange, User partner) {
                if (partner == null || partner.getId() == null) {
                    Toast.makeText(ExchangesActivity.this, "Cannot review: no account id.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(ExchangesActivity.this, RateReviewActivity.class);
                intent.putExtra("partnerUser", partner);
                startActivity(intent);
            }

            @Override
            public void onDetails(ExchangeSummary exchange, User partner) {
                openDetails(exchange);
            }
        });

        binding.rvExchanges.setLayoutManager(new LinearLayoutManager(this));
        binding.rvExchanges.setAdapter(adapter);

        binding.ivBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExchanges();
    }

    private void loadExchanges() {
        repository.fetchMyExchanges(new SkillBarterRepository.DataCallback<List<ExchangeSummary>>() {
            @Override
            public void onSuccess(List<ExchangeSummary> exchanges) {
                if (isFinishing() || isDestroyed()) return;

                boolean empty = exchanges == null || exchanges.isEmpty();
                binding.tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                binding.rvExchanges.setVisibility(empty ? View.GONE : View.VISIBLE);
                adapter.setExchanges(exchanges);

                loadReviewedPartners();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) return;
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.rvExchanges.setVisibility(View.GONE);
                binding.tvEmpty.setText(message);
                Toast.makeText(ExchangesActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Tells the adapter which rows are already rated. */
    private void loadReviewedPartners() {
        repository.fetchMyReviews(new SkillBarterRepository.DataCallback<List<Review>>() {
            @Override
            public void onSuccess(List<Review> reviews) {
                if (isFinishing() || isDestroyed()) return;
                List<String> ids = new ArrayList<>();
                if (reviews != null) {
                    for (Review review : reviews) {
                        if (review.getReviewedUserId() != null) {
                            ids.add(review.getReviewedUserId());
                        }
                    }
                }
                adapter.setReviewed(ids);
            }

            @Override
            public void onError(String message) {
                // Non-critical: every completed row keeps its Rate button.
            }
        });
    }

    /** Opens the details screen for an exchange. */
    private void openDetails(ExchangeSummary exchange) {
        if (exchange.getPartner() == null) return;
        Intent intent = new Intent(this, ExchangeDetailsActivity.class);
        intent.putExtra("partnerUser", exchange.getPartner());
        intent.putExtra("requestId", exchange.getRequestId());
        intent.putExtra("requestedSkill", exchange.getRequestedSkill());
        intent.putExtra("offeredSkill", exchange.getOfferedSkill());
        intent.putExtra("direction", exchange.getDirection());
        startActivity(intent);
    }
}