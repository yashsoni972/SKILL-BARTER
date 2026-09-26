package com.yashsoni.skillbarter.ui.marketplace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yashsoni.skillbarter.data.model.SkillListing;
import com.yashsoni.skillbarter.databinding.ActivityMarketplaceBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.requests.SendRequestActivity;

import java.util.List;

public class MarketplaceActivity extends AppCompatActivity {

    private ActivityMarketplaceBinding binding;
    private SkillBarterRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMarketplaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnOfferSkill.setOnClickListener(v -> {
            Toast.makeText(this, "To offer a skill listing, update your Offered Skills on Profile!", Toast.LENGTH_LONG).show();
        });

        loadListings();
    }

    private void loadListings() {
        repository.fetchMarketplaceListings(new SkillBarterRepository.DataCallback<List<SkillListing>>() {
            @Override
            public void onSuccess(List<SkillListing> listings) {
                SkillListingAdapter adapter = new SkillListingAdapter(listings, listing -> {
                    Intent intent = new Intent(MarketplaceActivity.this, SendRequestActivity.class);
                    if (listing.getUserId() != null) {
                        intent.putExtra("targetUser", listing.getUserId());
                    }
                    startActivity(intent);
                });
                binding.rvMarketplace.setLayoutManager(new LinearLayoutManager(MarketplaceActivity.this));
                binding.rvMarketplace.setAdapter(adapter);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MarketplaceActivity.this, "Unable to load marketplace", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
