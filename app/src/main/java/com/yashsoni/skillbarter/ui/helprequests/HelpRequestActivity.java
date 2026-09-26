package com.yashsoni.skillbarter.ui.helprequests;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yashsoni.skillbarter.data.model.HelpRequest;
import com.yashsoni.skillbarter.databinding.ActivityHelpRequestBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.requests.SendRequestActivity;

import java.util.List;

public class HelpRequestActivity extends AppCompatActivity {

    private ActivityHelpRequestBinding binding;
    private SkillBarterRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnPostHelp.setOnClickListener(v -> {
            Toast.makeText(this, "To post a help request, add desired skill to 'Wanted Skills' in Profile!", Toast.LENGTH_LONG).show();
        });

        loadHelpRequests();
    }

    private void loadHelpRequests() {
        repository.fetchHelpRequests(new SkillBarterRepository.DataCallback<List<HelpRequest>>() {
            @Override
            public void onSuccess(List<HelpRequest> requests) {
                HelpRequestAdapter adapter = new HelpRequestAdapter(requests, request -> {
                    Intent intent = new Intent(HelpRequestActivity.this, SendRequestActivity.class);
                    if (request.getUserId() != null) {
                        intent.putExtra("targetUser", request.getUserId());
                    }
                    startActivity(intent);
                });
                binding.rvHelpRequests.setLayoutManager(new LinearLayoutManager(HelpRequestActivity.this));
                binding.rvHelpRequests.setAdapter(adapter);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(HelpRequestActivity.this, "Unable to load help requests", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
