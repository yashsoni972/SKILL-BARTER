package com.yashsoni.skillbarter.ui.availability;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.yashsoni.skillbarter.data.model.Availability;
import com.yashsoni.skillbarter.databinding.ActivityAvailabilityBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;

import java.util.List;

public class AvailabilityActivity extends AppCompatActivity implements AvailabilityAdapter.OnDeleteListener {

    private static final String[] DAYS = {
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };
    private static final String[] START_TIMES = {
            "6:00 AM", "8:00 AM", "10:00 AM", "12:00 PM", "2:00 PM",
            "4:00 PM", "6:00 PM", "8:00 PM", "10:00 PM"
    };
    private static final String[] MODES = { "Online", "In person" };

    private ActivityAvailabilityBinding binding;
    private SkillBarterRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAvailabilityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        binding.ivBack.setOnClickListener(v -> finish());
        binding.btnAddSlot.setOnClickListener(v -> showAddDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSlots();
    }

    /**
     * Loads slots from the database. This screen previously rendered three
     * hardcoded strings from the layout file and never read or wrote real data.
     */
    private void loadSlots() {
        repository.fetchMyAvailability(new SkillBarterRepository.DataCallback<List<Availability>>() {
            @Override
            public void onSuccess(List<Availability> slots) {
                binding.rvSlots.setLayoutManager(new LinearLayoutManager(AvailabilityActivity.this));
                binding.rvSlots.setAdapter(new AvailabilityAdapter(slots, AvailabilityActivity.this));

                boolean empty = slots == null || slots.isEmpty();
                binding.tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                binding.rvSlots.setVisibility(empty ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AvailabilityActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAddDialog() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, 0);

        final Spinner daySpinner = addPicker(root, "Day", DAYS, 0);
        final Spinner startSpinner = addPicker(root, "Start time", START_TIMES, 6);
        final Spinner endSpinner = addPicker(root, "End time", START_TIMES, 7);
        final Spinner modeSpinner = addPicker(root, "Mode", MODES, 0);

        // Keep the end time after the start time so ranges are never inverted.
        startSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                if (endSpinner.getSelectedItemPosition() <= position) {
                    endSpinner.setSelection(Math.min(position + 1, START_TIMES.length - 1));
                }
            }
        });

        new AlertDialog.Builder(this)
                .setTitle("Add Time Slot")
                .setView(root)
                .setPositiveButton("Save", (dialog, which) -> saveSlot(
                        DAYS[daySpinner.getSelectedItemPosition()],
                        START_TIMES[startSpinner.getSelectedItemPosition()],
                        START_TIMES[endSpinner.getSelectedItemPosition()],
                        MODES[modeSpinner.getSelectedItemPosition()]))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private Spinner addPicker(LinearLayout parent, String label, String[] options, int selected) {
        TextView header = new TextView(this);
        header.setText(label);
        header.setTextSize(12);
        header.setPadding(0, 12, 0, 4);
        parent.addView(header);

        Spinner spinner = new Spinner(this);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options));
        spinner.setSelection(selected);
        parent.addView(spinner);
        return spinner;
    }

    private void saveSlot(String day, String start, String end, String mode) {
        if (!isValidRange(start, end)) {
            Toast.makeText(this, "End time must be later than start time.", Toast.LENGTH_LONG).show();
            return;
        }

        binding.btnAddSlot.setEnabled(false);
        repository.addAvailability(day, start, end, mode, new SkillBarterRepository.DataCallback<Availability>() {
            @Override
            public void onSuccess(Availability slot) {
                binding.btnAddSlot.setEnabled(true);
                Toast.makeText(AvailabilityActivity.this, "Saved: " + slot.getDay() + " " + slot.getStartTime() + " - " + slot.getEndTime(),
                        Toast.LENGTH_SHORT).show();
                loadSlots();
            }

            @Override
            public void onError(String message) {
                binding.btnAddSlot.setEnabled(true);
                Toast.makeText(AvailabilityActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isValidRange(String start, String end) {
        return indexOf(START_TIMES, start) < indexOf(START_TIMES, end);
    }

    private int indexOf(String[] arr, String value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(value)) return i;
        }
        return -1;
    }

    @Override
    public void onDelete(Availability slot) {
        if (slot.getId() == null) {
            Toast.makeText(this, "Cannot remove this slot: it has no id.", Toast.LENGTH_LONG).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Remove slot?")
                .setMessage(slot.getDay() + " " + slot.getStartTime() + " - " + slot.getEndTime())
                .setPositiveButton("Remove", (dialog, which) ->
                        repository.deleteAvailability(slot.getId(), new SkillBarterRepository.DataCallback<JsonObject>() {
                            @Override
                            public void onSuccess(JsonObject ignored) {
                                Toast.makeText(AvailabilityActivity.this, "Slot removed", Toast.LENGTH_SHORT).show();
                                loadSlots();
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(AvailabilityActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        }))
                .setNegativeButton("Keep", null)
                .show();
    }

    /** Spinner listener that only requires the position, to keep call sites short. */
    private abstract static class SimpleItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        abstract void onItemSelected(int position);

        @Override
        public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
            onItemSelected(position);
        }

        @Override
        public void onNothingSelected(android.widget.AdapterView<?> parent) { }
    }
}
