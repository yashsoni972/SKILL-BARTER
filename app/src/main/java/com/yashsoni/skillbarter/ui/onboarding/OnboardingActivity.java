package com.yashsoni.skillbarter.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.databinding.ActivityOnboardingBinding;
import com.yashsoni.skillbarter.ui.auth.LoginActivity;
import com.yashsoni.skillbarter.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private SessionManager sessionManager;

    private static class Slide {
        String title;
        String description;
        int imageRes;

        Slide(String title, String description, int imageRes) {
            this.title = title;
            this.description = description;
            this.imageRes = imageRes;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        List<Slide> slides = new ArrayList<>();
        slides.add(new Slide("Share What You Know", "Offer your skills and help others learn while expanding your network.", R.drawable.ic_logo));
        slides.add(new Slide("Discover New Skills", "Find talented people who can teach what you want to learn.", R.drawable.ic_search));
        slides.add(new Slide("Grow Together", "Exchange knowledge, build real connections and grow together.", R.drawable.ic_swap));

        OnboardingAdapter adapter = new OnboardingAdapter(slides);
        binding.viewPager.setAdapter(adapter);

        binding.btnNext.setOnClickListener(v -> {
            int current = binding.viewPager.getCurrentItem();
            if (current < slides.size() - 1) {
                binding.viewPager.setCurrentItem(current + 1);
            } else {
                completeOnboarding();
            }
        });

        binding.btnSkip.setOnClickListener(v -> completeOnboarding());
    }

    private void completeOnboarding() {
        sessionManager.setOnboardingCompleted(true);
        startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
        finish();
    }

    private static class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder> {
        private final List<Slide> slides;

        OnboardingAdapter(List<Slide> slides) {
            this.slides = slides;
        }

        @NonNull
        @Override
        public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding_slide, parent, false);
            return new SlideViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
            Slide slide = slides.get(position);
            holder.tvTitle.setText(slide.title);
            holder.tvDescription.setText(slide.description);
            holder.ivImage.setImageResource(slide.imageRes);
        }

        @Override
        public int getItemCount() {
            return slides.size();
        }

        static class SlideViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDescription;
            ImageView ivImage;

            SlideViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDescription = itemView.findViewById(R.id.tvDescription);
                ivImage = itemView.findViewById(R.id.ivSlideImage);
            }
        }
    }
}
