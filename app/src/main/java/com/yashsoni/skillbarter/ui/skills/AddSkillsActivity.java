package com.yashsoni.skillbarter.ui.skills;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivityAddSkillsBinding;
import com.yashsoni.skillbarter.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AddSkillsActivity extends AppCompatActivity {

    private ActivityAddSkillsBinding binding;
    private SessionManager sessionManager;
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSkillsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        currentTab = getIntent().getIntExtra("selectedTab", 0);
        if (binding.tabLayout.getTabAt(currentTab) != null) {
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(currentTab));
        }

        binding.ivBack.setOnClickListener(v -> finish());

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                updateCheckboxStates();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        updateCheckboxStates();

        binding.btnSave.setOnClickListener(v -> saveSkills());
    }

    private void updateCheckboxStates() {
        User user = sessionManager.getUser();
        List<String> list = (currentTab == 0) ? user.getOfferedSkills() : user.getWantedSkills();

        binding.cbHtml.setChecked(list != null && list.contains("HTML & CSS"));
        binding.cbJs.setChecked(list != null && list.contains("JavaScript"));
        binding.cbPython.setChecked(list != null && list.contains("Python"));
        binding.cbReact.setChecked(list != null && list.contains("React"));
        binding.cbGraphicDesign.setChecked(list != null && list.contains("Graphic Design"));
        binding.cbVideoEditing.setChecked(list != null && list.contains("Video Editing"));
        binding.cbUiUx.setChecked(list != null && list.contains("UI/UX"));
    }

    private void saveSkills() {
        User user = sessionManager.getUser();
        List<String> selected = new ArrayList<>();

        if (binding.cbHtml.isChecked()) selected.add("HTML & CSS");
        if (binding.cbJs.isChecked()) selected.add("JavaScript");
        if (binding.cbPython.isChecked()) selected.add("Python");
        if (binding.cbReact.isChecked()) selected.add("React");
        if (binding.cbGraphicDesign.isChecked()) selected.add("Graphic Design");
        if (binding.cbVideoEditing.isChecked()) selected.add("Video Editing");
        if (binding.cbUiUx.isChecked()) selected.add("UI/UX");

        String custom = binding.etCustomSkill.getText().toString().trim();
        if (!custom.isEmpty()) {
            selected.add(custom);
        }

        if (currentTab == 0) {
            user.setOfferedSkills(selected);
        } else {
            user.setWantedSkills(selected);
        }

        sessionManager.updateUser(user);
        Toast.makeText(this, "Skills updated successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
