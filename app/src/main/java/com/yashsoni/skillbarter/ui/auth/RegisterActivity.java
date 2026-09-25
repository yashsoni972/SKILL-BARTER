package com.yashsoni.skillbarter.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.MainActivity;
import com.yashsoni.skillbarter.api.ApiClient;
import com.yashsoni.skillbarter.data.model.AuthResponse;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivityRegisterBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        binding.btnRegister.setOnClickListener(v -> performRegister());

        binding.tvLoginLink.setOnClickListener(v -> finish());
    }

    private void performRegister() {
        String name = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String location = binding.etLocation.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnRegister.setEnabled(false);
        Toast.makeText(this, "Registering account with MongoDB cloud...", Toast.LENGTH_SHORT).show();

        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("email", email);
        body.put("password", password);
        body.put("location", location.isEmpty() ? "Anand, Gujarat" : location);
        body.put("bio", "Passionate about web development, design and learning new technologies.");

        ApiClient.getService(this).register(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                binding.btnRegister.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    AuthResponse authRes = response.body();
                    User registeredUser = authRes.getUser();
                    SkillBarterRepository.getInstance(RegisterActivity.this).registerUser(registeredUser);
                    sessionManager.createLoginSession(authRes.getToken(), registeredUser);
                    Toast.makeText(RegisterActivity.this, "🎉 Account Saved to MongoDB Atlas! Welcome " + registeredUser.getName(), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Fallback to local user session if server response is unexpected
                    completeLocalRegistration(name, email, location);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                binding.btnRegister.setEnabled(true);
                completeLocalRegistration(name, email, location);
            }
        });
    }

    private void completeLocalRegistration(String name, String email, String location) {
        User newUser = new User("user_" + System.currentTimeMillis(), name, email,
                location.isEmpty() ? "Anand, Gujarat" : location,
                "Passionate about web development, design and learning new technologies.", 5.0, 0);

        SkillBarterRepository.getInstance(this).registerUser(newUser);
        sessionManager.createLoginSession("mock_jwt_token_register_123", newUser);
        Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
