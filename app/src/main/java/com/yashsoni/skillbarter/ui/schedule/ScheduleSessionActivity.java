package com.yashsoni.skillbarter.ui.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yashsoni.skillbarter.data.model.Session;
import com.yashsoni.skillbarter.databinding.ActivityScheduleSessionBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.utils.SystemBars;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Lets both members agree when a session happens: day of week, date, and a start
 * and end time, plus the Google Meet room. Without this the only way to run a
 * session was to complete it immediately, so there was nothing to plan against.
 */
public class ScheduleSessionActivity extends AppCompatActivity {

    private static final String[] DAYS = {
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    private ActivityScheduleSessionBinding binding;
    private SkillBarterRepository repository;

    private final Calendar startTime = Calendar.getInstance();
    private final Calendar endTime = Calendar.getInstance();
    private final Calendar chosenDate = Calendar.getInstance();

    private String requestId;
    private String skill;
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
            skill = getIntent().getStringExtra("skill");
            partnerName = getIntent().getStringExtra("partnerName");
        }

        // Without a request id the backend cannot work out who the partner is,
        // so there is nothing sensible to schedule.
        if (TextUtils.isEmpty(requestId)) {
            Toast.makeText(this, "No exchange to schedule. Open the exchange from Chats or My Exchanges.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (TextUtils.isEmpty(skill)) {
            skill = "Skill exchange";
        }

        binding.tvSubtitle.setText(partnerName == null || partnerName.isEmpty()
                ? "Both members see the same slot. Sessions run on Google Meet."
                : "Scheduling with " + partnerName + ". Both of you will see the same slot.");

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, DAYS);
        binding.spinnerDay.setAdapter(dayAdapter);
        binding.spinnerDay.setSelection(todayIndex());

        // Sensible starting values so the screen is never empty on open.
        startTime.set(Calendar.HOUR_OF_DAY, 18);
        startTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.HOUR_OF_DAY, 19);
        endTime.set(Calendar.MINUTE, 0);

        showDate();
        showTimes();

        binding.etDate.setOnClickListener(v -> pickDate());
        binding.etStartTime.setOnClickListener(v -> pickTime(startTime, binding.etStartTime));
        binding.etEndTime.setOnClickListener(v -> pickTime(endTime, binding.etEndTime));

        binding.btnSave.setOnClickListener(v -> save());
    }

    /** Monday-first index of today, matching the {@link #DAYS} order. */
    private int todayIndex() {
        int calDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); // 1 = Sunday
        return (calDay + 5) % 7;
    }

    private void pickDate() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    chosenDate.set(year, month, dayOfMonth);
                    showDate();
                },
                chosenDate.get(Calendar.YEAR),
                chosenDate.get(Calendar.MONTH),
                chosenDate.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void pickTime(Calendar target, android.widget.TextView label) {
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    target.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    target.set(Calendar.MINUTE, minute);
                    showTimes();
                },
                target.get(Calendar.HOUR_OF_DAY),
                target.get(Calendar.MINUTE),
                false);
        dialog.show();
    }

    private void showDate() {
        binding.etDate.setText(new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(chosenDate.getTime()));
    }

    private void showTimes() {
        binding.etStartTime.setText(format(startTime));
        binding.etEndTime.setText(format(endTime));
    }

    private String format(Calendar c) {
        return String.format(Locale.getDefault(), "%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    private void save() {
        if (endTime.before(startTime)) {
            Toast.makeText(this, "The end time must be after the start time.", Toast.LENGTH_LONG).show();
            return;
        }

        binding.btnSave.setEnabled(false);

        String day = DAYS[binding.spinnerDay.getSelectedItemPosition()];
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(chosenDate.getTime());
        String meet = binding.etMeetLink.getText() == null ? "" : binding.etMeetLink.getText().toString().trim();

        repository.scheduleSession(requestId, skill, date, day,
                format(startTime), format(endTime), meet,
                new SkillBarterRepository.DataCallback<Session>() {
                    @Override
                    public void onSuccess(Session session) {
                        Toast.makeText(ScheduleSessionActivity.this, "Session scheduled", Toast.LENGTH_SHORT).show();
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