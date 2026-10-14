package com.yashsoni.skillbarter.ui.skills;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivityAddSkillsBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.utils.SessionManager;
import com.yashsoni.skillbarter.utils.SystemBars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddSkillsActivity extends AppCompatActivity {

    private ActivityAddSkillsBinding binding;
    private SessionManager sessionManager;
    private SkillBarterRepository repository;
    private int currentTab = 0;

    private static final List<String> PRESET_SKILLS = Arrays.asList(
            "HTML & CSS", "JavaScript", "Python", "React", "Graphic Design", "Video Editing", "UI/UX"
    );

    // Custom skills added dynamically (not in presets)
    private final List<String> customOffered = new ArrayList<>();
    private final List<String> customWanted = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSkillsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        sessionManager = new SessionManager(this);
        repository = SkillBarterRepository.getInstance(this);

        User user = sessionManager.getUser();
        // Pre-populate custom lists from existing skills not in presets
        if (user != null) {
            for (String s : safeList(user.getOfferedSkills()))
                if (!PRESET_SKILLS.contains(s)) customOffered.add(s);
            for (String s : safeList(user.getWantedSkills()))
                if (!PRESET_SKILLS.contains(s)) customWanted.add(s);
        }

        currentTab = getIntent().getIntExtra("selectedTab", 0);
        if (binding.tabLayout.getTabAt(currentTab) != null)
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(currentTab));

        binding.ivBack.setOnClickListener(v -> finish());

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                renderCustomChips();
                updateCheckboxStates();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        updateCheckboxStates();
        renderCustomChips();

        binding.btnSave.setOnClickListener(v -> saveSkills());

        // Add custom skill on button click
        binding.etCustomSkill.setOnEditorActionListener((v, actionId, event) -> {
            addCustomSkill();
            return true;
        });
        binding.btnAddCustom.setOnClickListener(v -> addCustomSkill());
    }

    private void addCustomSkill() {
        String skill = binding.etCustomSkill.getText().toString().trim();
        if (skill.isEmpty()) return;
        if (PRESET_SKILLS.contains(skill)) {
            Toast.makeText(this, "Already in the list above", Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> target = currentTab == 0 ? customOffered : customWanted;
        if (!target.contains(skill)) {
            target.add(skill);
            renderCustomChips();
        }
        binding.etCustomSkill.setText("");
    }

    private void renderCustomChips() {
        LinearLayout container = binding.layoutCustomSkills;
        container.removeAllViews();
        List<String> list = currentTab == 0 ? customOffered : customWanted;
        for (String skill : new ArrayList<>(list)) {
            View chip = LayoutInflater.from(this).inflate(R.layout.item_skill_chip, container, false);
            ((TextView) chip.findViewById(R.id.tvSkillName)).setText(skill);
            chip.findViewById(R.id.ivRemove).setOnClickListener(v -> {
                list.remove(skill);
                renderCustomChips();
            });
            container.addView(chip);
        }
    }

    private void updateCheckboxStates() {
        User user = sessionManager.getUser();
        List<String> list = currentTab == 0 ? safeList(user != null ? user.getOfferedSkills() : null)
                : safeList(user != null ? user.getWantedSkills() : null);

        binding.cbHtml.setChecked(list.contains("HTML & CSS"));
        binding.cbJs.setChecked(list.contains("JavaScript"));
        binding.cbPython.setChecked(list.contains("Python"));
        binding.cbReact.setChecked(list.contains("React"));
        binding.cbGraphicDesign.setChecked(list.contains("Graphic Design"));
        binding.cbVideoEditing.setChecked(list.contains("Video Editing"));
        binding.cbUiUx.setChecked(list.contains("UI/UX"));
    }

    private void saveSkills() {
        // Add any pending custom skill in the text field
        String pending = binding.etCustomSkill.getText().toString().trim();
        if (!pending.isEmpty()) addCustomSkill();

        User user = sessionManager.getUser();
        List<String> selected = new ArrayList<>();
        if (binding.cbHtml.isChecked()) selected.add("HTML & CSS");
        if (binding.cbJs.isChecked()) selected.add("JavaScript");
        if (binding.cbPython.isChecked()) selected.add("Python");
        if (binding.cbReact.isChecked()) selected.add("React");
        if (binding.cbGraphicDesign.isChecked()) selected.add("Graphic Design");
        if (binding.cbVideoEditing.isChecked()) selected.add("Video Editing");
        if (binding.cbUiUx.isChecked()) selected.add("UI/UX");

        List<String> customs = currentTab == 0 ? customOffered : customWanted;
        selected.addAll(customs);

        if (currentTab == 0) user.setOfferedSkills(selected);
        else user.setWantedSkills(selected);
        sessionManager.updateUser(user);

        // Save BOTH offered and wanted to backend
        Map<String, List<String>> body = new HashMap<>();
        body.put("offeredSkills", safeList(user.getOfferedSkills()));
        body.put("wantedSkills", safeList(user.getWantedSkills()));

        binding.btnSave.setEnabled(false);
        repository.updateSkillsBatch(body, new SkillBarterRepository.DataCallback<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> data) {
                Toast.makeText(AddSkillsActivity.this, "Skills saved!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                binding.btnSave.setEnabled(true);
                // Local save already done; show warning but don't block
                Toast.makeText(AddSkillsActivity.this, "Saved locally. Sync failed: " + message, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private List<String> safeList(List<String> list) {
        return list != null ? list : new ArrayList<>();
    }
}
