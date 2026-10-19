package com.yashsoni.skillbarter.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yashsoni.skillbarter.data.model.Attachment;
import com.yashsoni.skillbarter.data.model.Message;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivityChatBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.exchange.ExchangeDetailsActivity;
import com.yashsoni.skillbarter.ui.schedule.ScheduleSessionActivity;
import com.yashsoni.skillbarter.utils.SessionManager;
import com.yashsoni.skillbarter.utils.SnackUtil;
import com.yashsoni.skillbarter.utils.SystemBars;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final int REQUEST_SCHEDULE = 1001;
    private static final int REQUEST_PICK_FILE = 1002;
    private static final long MAX_UPLOAD_BYTES = 10 * 1024 * 1024;

    private ActivityChatBinding binding;
    private SkillBarterRepository repository;
    private User partnerUser;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private String requestId;
    private String requestedSkill;
    private String offeredSkill;
    private String direction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        repository = SkillBarterRepository.getInstance(this);

        if (getIntent() != null && getIntent().hasExtra("partnerUser")) {
            partnerUser = (User) getIntent().getSerializableExtra("partnerUser");
            requestId = getIntent().getStringExtra("requestId");
            requestedSkill = getIntent().getStringExtra("requestedSkill");
            offeredSkill = getIntent().getStringExtra("offeredSkill");
            direction = getIntent().getStringExtra("direction");
        }
        if (partnerUser == null) {
            Toast.makeText(this, "No conversation partner selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (partnerUser.getId() == null || partnerUser.getId().isEmpty()) {
            Toast.makeText(this, "This user has no account id. Please refresh and try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        binding.tvUserName.setText(partnerUser.getName());

        binding.ivBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        messageList = new ArrayList<>();
        User currentUser = new SessionManager(this).getUser();
        adapter = new MessageAdapter(messageList, currentUser != null ? currentUser.getId() : null, this::openAttachment);

        boolean iAmSender = "outgoing".equals(direction);
        String mySkill = iAmSender ? offeredSkill : requestedSkill;
        String theirSkill = iAmSender ? requestedSkill : offeredSkill;
        binding.tvYouTeach.setText("You teach\n" + (mySkill != null && !mySkill.isEmpty() ? mySkill : "—"));
        binding.tvYouLearn.setText("You learn\n" + (theirSkill != null && !theirSkill.isEmpty() ? theirSkill : "—"));

        binding.layoutSkillBanner.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, ExchangeDetailsActivity.class);
            intent.putExtra("partnerUser", partnerUser);
            intent.putExtra("requestId", requestId);
            intent.putExtra("requestedSkill", requestedSkill);
            intent.putExtra("offeredSkill", offeredSkill);
            intent.putExtra("direction", direction);
            startActivity(intent);
        });

        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMessages.setAdapter(adapter);

        loadMessages();

        binding.btnSend.setOnClickListener(v -> {
            String text = binding.etMessage.getText().toString().trim();
            if (text.isEmpty()) return;

            binding.btnSend.setEnabled(false);
            repository.sendMessageApi(partnerUser.getId(), text, new SkillBarterRepository.DataCallback<Message>() {
                @Override
                public void onSuccess(Message message) {
                    binding.btnSend.setEnabled(true);
                    binding.etMessage.setText("");
                    loadMessages();
                }

                @Override
                public void onError(String message) {
                    binding.btnSend.setEnabled(true);
                    Toast.makeText(ChatActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });

        binding.btnSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, ScheduleSessionActivity.class);
            intent.putExtra("requestId", requestId);
            intent.putExtra("partnerName", partnerUser.getName());
            intent.putExtra("mySkill", mySkill);
            intent.putExtra("theirSkill", theirSkill);
            startActivityForResult(intent, REQUEST_SCHEDULE);
        });

        binding.btnAttach.setOnClickListener(v -> pickFile());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCHEDULE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Session scheduled!", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_PICK_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uploadPickedFile(data.getData());
        }
    }

    private void loadMessages() {
        repository.fetchMessages(partnerUser.getId(), new SkillBarterRepository.DataCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                messageList.clear();
                messageList.addAll(messages);
                adapter.notifyDataSetChanged();
                if (!messageList.isEmpty()) {
                    binding.rvMessages.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "image/jpeg",
            "image/png"
        });
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(intent, REQUEST_PICK_FILE);
        } catch (Exception e) {
            Toast.makeText(this, "No file picker is available on this device", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadPickedFile(Uri uri) {
        String mime = getContentResolver().getType(uri);
        String name = queryDisplayName(uri);

        File source = copyToCache(uri, name);

        if (source == null) {
            SnackUtil.show(this, "That file could not be read");
            return;
        }
        if (source.length() > MAX_UPLOAD_BYTES) {
            SnackUtil.show(this, "File is larger than the 10 MB limit");
            return;
        }

        binding.btnAttach.setEnabled(false);

        repository.uploadAttachment(source, mime == null ? "application/octet-stream" : mime, requestId, partnerUser.getId(),
                new SkillBarterRepository.DataCallback<Attachment>() {
                    @Override
                    public void onSuccess(Attachment attachment) {
                        binding.btnAttach.setEnabled(true);
                        String caption = binding.etMessage.getText().toString().trim();
                        sendWithAttachment(caption, attachment.getId());
                    }

                    @Override
                    public void onError(String message) {
                        binding.btnAttach.setEnabled(true);
                        Toast.makeText(ChatActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendWithAttachment(String text, String attachmentId) {
        repository.sendMessageWithAttachment(partnerUser.getId(), text, attachmentId, new SkillBarterRepository.DataCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                binding.etMessage.setText("");
                loadMessages();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openAttachment(Attachment attachment) {
        Toast.makeText(this, "Opening " + attachment.getFilename(), Toast.LENGTH_SHORT).show();
        repository.mintAttachmentLink(attachment.getId(), new SkillBarterRepository.DataCallback<String>() {
            @Override
            public void onSuccess(String url) {
                if (attachment.isPdf()) {
                    PdfViewerActivity.start(ChatActivity.this, url, attachment.getFilename());
                } else if (attachment.isImage()) {
                    Intent view = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivitySafely(view, attachment.getFilename());
                } else {
                    Intent view = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivitySafely(view, attachment.getFilename());
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startActivitySafely(Intent intent, String filename) {
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No app can open " + filename, Toast.LENGTH_LONG).show();
        }
    }

    static void openExternalFile(Context context, String url, String filename) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "No app can open " + filename, Toast.LENGTH_LONG).show();
        }
    }

    private String queryDisplayName(Uri uri) {
        String name = "shared_file";
        try (android.database.Cursor c = getContentResolver().query(uri, null, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                int idx = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                if (idx >= 0 && c.getString(idx) != null) name = c.getString(idx);
            }
        } catch (Exception ignored) {
        }
        return name;
    }

    private File copyToCache(Uri uri, String name) {
        File out = new File(getCacheDir(), "upload_" + System.currentTimeMillis() + "_" + name);
        try (InputStream in = getContentResolver().openInputStream(uri);
             FileOutputStream fos = new FileOutputStream(out)) {
            if (in == null) return null;
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) fos.write(buffer, 0, read);
            return out;
        } catch (Exception e) {
            return null;
        }
    }
}
