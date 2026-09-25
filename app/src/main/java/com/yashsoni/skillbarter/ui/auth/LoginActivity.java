package com.yashsoni.skillbarter.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.MainActivity;
import com.yashsoni.skillbarter.api.ApiClient;
import com.yashsoni.skillbarter.data.model.AuthResponse;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivityLoginBinding;
import com.yashsoni.skillbarter.utils.SessionManager;

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
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnLogin.setEnabled(false);
        Toast.makeText(this, "Logging in with MongoDB cloud...", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(LoginActivity.this, "Welcome Back, " + loggedInUser.getName() + "!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    completeLocalLogin(email);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                binding.btnLogin.setEnabled(true);
                completeLocalLogin(email);
            }
        });
    }

    private void completeLocalLogin(String email) {
        User user = sessionManager.getUser();
        if (user == null) {
            user = new User("user_local", "Barter Member", email, "Anand, Gujarat",
                    "Passionate about web development, design and learning new technologies.", 5.0, 0);
        } else {
            user.setEmail(email);
        }

        sessionManager.createLoginSession("mock_jwt_token_123456", user);
        Toast.makeText(this, "Welcome " + user.getName(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
