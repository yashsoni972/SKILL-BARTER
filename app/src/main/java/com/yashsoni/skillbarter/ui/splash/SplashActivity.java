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

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

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
