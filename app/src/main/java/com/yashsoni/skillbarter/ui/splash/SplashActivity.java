package com.yashsoni.skillbarter.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

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
    private boolean hasNavigated = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        sessionManager = new SessionManager(this);

        // Wake the Render instance while the animation plays
        com.yashsoni.skillbarter.api.ApiClient.getService(this)
                .getStats().enqueue(new retrofit2.Callback<
                        com.yashsoni.skillbarter.data.model.Stats>() {
                    @Override public void onResponse(
                            retrofit2.Call<com.yashsoni.skillbarter.data.model.Stats> c,
                            retrofit2.Response<com.yashsoni.skillbarter.data.model.Stats> r) {}
                    @Override public void onFailure(
                            retrofit2.Call<com.yashsoni.skillbarter.data.model.Stats> c,
                            Throwable t) {}
                });

        startEntranceAnimations();

        binding.btnGetStarted.setOnClickListener(v -> navigateNext());
        handler.postDelayed(this::navigateNext, 3000);
    }

    // ── Entrance animations ──────────────────────────────────────────────────

    private void startEntranceAnimations() {
        // Hide everything initially
        binding.orbTopRight.setScaleX(0.4f);
        binding.orbTopRight.setScaleY(0.4f);
        binding.orbTopRight.setAlpha(0f);

        binding.orbBottomLeft.setScaleX(0.4f);
        binding.orbBottomLeft.setScaleY(0.4f);
        binding.orbBottomLeft.setAlpha(0f);

        binding.orbMidRight.setAlpha(0f);

        binding.ivLogo.setScaleX(0.2f);
        binding.ivLogo.setScaleY(0.2f);
        binding.ivLogo.setAlpha(0f);
        binding.ivLogo.setRotation(-15f);

        binding.layoutSplashContent.setTranslationY(80f);
        binding.layoutSplashContent.setAlpha(0f);

        binding.btnGetStarted.setTranslationY(40f);
        binding.btnGetStarted.setAlpha(0f);

        binding.viewPulseOuter.setAlpha(0f);
        binding.viewPulseInner.setAlpha(0f);

        // Orbs float in
        binding.orbTopRight.animate()
                .scaleX(1f).scaleY(1f).alpha(0.55f)
                .setDuration(900).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator(2f)).start();

        binding.orbBottomLeft.animate()
                .scaleX(1f).scaleY(1f).alpha(0.45f)
                .setDuration(900).setStartDelay(200)
                .setInterpolator(new DecelerateInterpolator(2f)).start();

        binding.orbMidRight.animate()
                .alpha(0.2f).setDuration(600).setStartDelay(400).start();

        // Logo pops in with overshoot, then starts pulse
        binding.ivLogo.animate()
                .scaleX(1f).scaleY(1f).alpha(1f).rotation(0f)
                .setDuration(750).setStartDelay(300)
                .setInterpolator(new OvershootInterpolator(1.4f))
                .withEndAction(this::startPulseLoop)
                .start();

        // Content slides up
        binding.layoutSplashContent.animate()
                .translationY(0f).alpha(1f)
                .setDuration(700).setStartDelay(500)
                .setInterpolator(new DecelerateInterpolator(1.5f)).start();

        // Button rises last
        binding.btnGetStarted.animate()
                .translationY(0f).alpha(1f)
                .setDuration(600).setStartDelay(800)
                .setInterpolator(new DecelerateInterpolator(1.5f)).start();
    }

    // ── Pulse ring animation (repeats every 1.6 s) ───────────────────────────

    private void startPulseLoop() {
        pulseSingleRing(binding.viewPulseOuter, 0);
        pulseSingleRing(binding.viewPulseInner, 400);
    }

    private void pulseSingleRing(android.view.View ring, long startDelay) {
        ring.setScaleX(0.85f);
        ring.setScaleY(0.85f);
        ring.setAlpha(0.7f);
        ring.animate()
                .scaleX(1.55f).scaleY(1.55f).alpha(0f)
                .setDuration(1400)
                .setStartDelay(startDelay)
                .setInterpolator(new DecelerateInterpolator(1.8f))
                .withEndAction(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        pulseSingleRing(ring, 0);
                    }
                })
                .start();
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    private void navigateNext() {
        if (hasNavigated || isFinishing() || isDestroyed()) return;
        hasNavigated = true;
        handler.removeCallbacksAndMessages(null);

        Intent intent;
        if (sessionManager.isLoggedIn()) {
            intent = new Intent(this, MainActivity.class);
        } else if (!sessionManager.isOnboardingCompleted()) {
            intent = new Intent(this, OnboardingActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }
}
