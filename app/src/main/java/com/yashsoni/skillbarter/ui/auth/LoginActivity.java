package com.yashsoni.skillbarter.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import com.yashsoni.skillbarter.utils.SnackUtil;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.MainActivity;
import com.yashsoni.skillbarter.api.ApiClient;
import com.yashsoni.skillbarter.data.model.AuthResponse;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivityLoginBinding;
import com.yashsoni.skillbarter.utils.PasswordToggle;
import com.yashsoni.skillbarter.utils.SessionManager;
import com.yashsoni.skillbarter.utils.SystemBars;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        PasswordToggle.attach(binding.etPassword, binding.btnTogglePassword);

        sessionManager = new SessionManager(this);

        binding.btnLogin.setOnClickListener(v -> performLogin());

        binding.btnCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void performLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            SnackUtil.show(this, "Please enter email and password");
            return;
        }

        binding.btnLogin.setEnabled(false);

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        ApiClient.getService(this).login(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                binding.btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    AuthResponse authRes = response.body();
                    User loggedInUser = authRes.getUser();
                    sessionManager.createLoginSession(authRes.getToken(), loggedInUser);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Login failed. Invalid credentials.";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    SnackUtil.showLong(LoginActivity.this, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                binding.btnLogin.setEnabled(true);
                SnackUtil.showLong(LoginActivity.this, "Network error. Please check your connection.");
            }
        });
    }
}
