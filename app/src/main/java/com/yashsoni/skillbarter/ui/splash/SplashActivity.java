package com.yashsoni.skillbarter.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.MainActivity;
import com.yashsoni.skillbarter.databinding.ActivitySplashBinding;
import com.yashsoni.skillbarter.ui.auth.LoginActivity;
import com.yashsoni.skillbarter.ui.onboarding.OnboardingActivity;
import com.yashsoni.skillbarter.utils.SessionManager;
import com.yashsoni.skillbarter.utils.SystemBars;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        sessionManager = new SessionManager(this);

        // Wake up Render cloud server in background immediately
        com.yashsoni.skillbarter.api.ApiClient.getService(this).getStats().enqueue(new retrofit2.Callback<com.yashsoni.skillbarter.data.model.Stats>() {
            @Override
            public void onResponse(retrofit2.Call<com.yashsoni.skillbarter.data.model.Stats> call, retrofit2.Response<com.yashsoni.skillbarter.data.model.Stats> response) {}
            @Override
            public void onFailure(retrofit2.Call<com.yashsoni.skillbarter.data.model.Stats> call, Throwable t) {}
        });

        // High-end motion entrance animation
        binding.ivLogo.setScaleX(0.3f);
        binding.ivLogo.setScaleY(0.3f);
        binding.ivLogo.setAlpha(0f);
        binding.ivLogo.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(800)
                .setInterpolator(new android.view.animation.OvershootInterpolator(1.2f))
                .start();

        binding.layoutSplashContent.setTranslationY(60f);
        binding.layoutSplashContent.setAlpha(0f);
        binding.layoutSplashContent.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(700)
                .setStartDelay(200)
                .start();

        binding.btnGetStarted.setOnClickListener(v -> navigateNext());

        // Auto transition after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateNext, 2000);
    }

    private void navigateNext() {
        if (isFinishing()) return;

        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else if (!sessionManager.isOnboardingCompleted()) {
            startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish();
    }
}
