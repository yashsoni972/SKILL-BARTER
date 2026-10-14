package com.yashsoni.skillbarter.ui.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.data.model.Session;
import com.yashsoni.skillbarter.databinding.ActivityScheduleSessionBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.utils.SystemBars;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleSessionActivity extends AppCompatActivity {

    private ActivityScheduleSessionBinding binding;
    private SkillBarterRepository repository;

    private final Calendar startCal = Calendar.getInstance();
    private final Calendar endCal = Calendar.getInstance();
    private final Calendar dateCal = Calendar.getInstance();

    private String requestId;
    private String mySkill;
    private String theirSkill;
    private String partnerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleSessionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        if (getIntent() != null) {
            requestId = getIntent().getStringExtra("requestId");
            mySkill = getIntent().getStringExtra("mySkill");
            theirSkill = getIntent().getStringExtra("theirSkill");
            // legacy fallback
            if (mySkill == null) mySkill = getIntent().getStringExtra("skill");
            partnerName = getIntent().getStringExtra("partnerName");
        }

        if (TextUtils.isEmpty(requestId)) {
            Toast.makeText(this, "No exchange to schedule.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        binding.tvMySkill.setText(mySkill != null && !mySkill.isEmpty() ? mySkill : "—");
        binding.tvTheirSkill.setText(theirSkill != null && !theirSkill.isEmpty() ? theirSkill : "—");

        // Default times: today 18:00 – 19:00
        startCal.set(Calendar.HOUR_OF_DAY, 18);
        startCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.HOUR_OF_DAY, 19);
        endCal.set(Calendar.MINUTE, 0);

        refreshDateLabel();
        refreshTimeLabels();

        binding.etDate.setOnClickListener(v -> pickDate());
        binding.etStartTime.setOnClickListener(v -> pickTime(startCal, true));
        binding.etEndTime.setOnClickListener(v -> pickTime(endCal, false));
        binding.ivBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> save());
    }

    private void pickDate() {
        new DatePickerDialog(this,
                (view, year, month, day) -> {
                    dateCal.set(year, month, day);
                    refreshDateLabel();
                },
                dateCal.get(Calendar.YEAR),
                dateCal.get(Calendar.MONTH),
                dateCal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void pickTime(Calendar target, boolean isStart) {
        new TimePickerDialog(this,
                (view, hour, minute) -> {
                    target.set(Calendar.HOUR_OF_DAY, hour);
                    target.set(Calendar.MINUTE, minute);
                    refreshTimeLabels();
                },
                target.get(Calendar.HOUR_OF_DAY),
                target.get(Calendar.MINUTE),
                false).show();
    }

    private void refreshDateLabel() {
        binding.etDate.setText(new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(dateCal.getTime()));
    }

    private void refreshTimeLabels() {
        binding.etStartTime.setText(fmt(startCal));
        binding.etEndTime.setText(fmt(endCal));
    }

    private String fmt(Calendar c) {
        return String.format(Locale.getDefault(), "%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    private void save() {
        if (endCal.before(startCal)) {
            Toast.makeText(this, "End time must be after start time.", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.btnSave.setEnabled(false);

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(dateCal.getTime());
        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.US).format(dateCal.getTime());
        String meet = binding.etMeetLink.getText() == null ? "" : binding.etMeetLink.getText().toString().trim();
        String skill = mySkill != null && !mySkill.isEmpty() ? mySkill : "Skill exchange";

        repository.scheduleSession(requestId, skill, date, dayOfWeek,
                fmt(startCal), fmt(endCal), meet,
                new SkillBarterRepository.DataCallback<Session>() {
                    @Override
                    public void onSuccess(Session session) {
                        Toast.makeText(ScheduleSessionActivity.this,
                                "Time proposed! Your partner will see it in Exchange Details.", Toast.LENGTH_LONG).show();
                        Intent result = new Intent();
                        result.putExtra("scheduled", true);
                        setResult(RESULT_OK, result);
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        binding.btnSave.setEnabled(true);
                        Toast.makeText(ScheduleSessionActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
