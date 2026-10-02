package com.yashsoni.skillbarter.ui.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yashsoni.skillbarter.data.model.Message;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.databinding.ActivityChatBinding;
import com.yashsoni.skillbarter.repository.SkillBarterRepository;
import com.yashsoni.skillbarter.ui.exchange.ExchangeDetailsActivity;
import com.yashsoni.skillbarter.ui.schedule.ScheduleSessionActivity;
import com.yashsoni.skillbarter.utils.SessionManager;
import com.yashsoni.skillbarter.utils.SystemBars;

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

        // A user object without an id cannot address any API call, and letting
        // that reach Retrofit produces the confusing
        // 'Path parameter "userId" value must not be null' network error.
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

        // Agreeing a date and time is the point of this button, so it goes straight to
        // the scheduling screen. Exchange details stay reachable from there.
        binding.btnSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, ScheduleSessionActivity.class);
            intent.putExtra("requestId", requestId);
            intent.putExtra("partnerName", partnerUser.getName());
            intent.putExtra("skill", teachingOrLearning());
            startActivityForResult(intent, REQUEST_SCHEDULE);
        });

        binding.btnAttach.setOnClickListener(v -> pickFile());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCHEDULE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Session scheduled", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_PICK_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uploadPickedFile(data.getData());
        }
    }

    /**
     * The skill this member is teaching in this exchange, falling back to the one
     * they asked for. Used as the subject line for the booked session.
     */
    private String teachingOrLearning() {
        boolean iAmSender = "outgoing".equals(direction);
        String taught = iAmSender ? offeredSkill : requestedSkill;
        String learned = iAmSender ? requestedSkill : offeredSkill;
        if (taught != null && !taught.trim().isEmpty()) return taught;
        if (learned != null && !learned.trim().isEmpty()) return learned;
        return "Skill exchange";
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

    /** Opens the system file picker, limited to the formats the server accepts. */
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

    /**
     * Copies the chosen file into the cache, because a content:// uri is not a
     * readable path and the upload needs real bytes.
     */
    private void uploadPickedFile(Uri uri) {
        String mime = getContentResolver().getType(uri);
        String name = queryDisplayName(uri);

        java.io.File source = copyToCache(uri, name);

        if (source == null) {
            Toast.makeText(this, "That file could not be read", Toast.LENGTH_LONG).show();
            return;
        }
        if (source.length() > MAX_UPLOAD_BYTES) {
            Toast.makeText(this, "That file is larger than the 10 MB limit", Toast.LENGTH_LONG).show();
            return;
        }

        binding.btnAttach.setEnabled(false);
        Toast.makeText(this, "Uploading " + name, Toast.LENGTH_SHORT).show();

        repository.uploadAttachment(source, mime == null ? "application/octet-stream" : mime, requestId,
                new SkillBarterRepository.DataCallback<com.yashsoni.skillbarter.data.model.Attachment>() {
                    @Override
                    public void onSuccess(com.yashsoni.skillbarter.data.model.Attachment attachment) {
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

    private void sendWithAttachment(String caption, String attachmentId) {
        repository.sendMessageWithAttachment(partnerUser.getId(), caption, attachmentId,
                new SkillBarterRepository.DataCallback<Message>() {
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

    /**
     * Images and PDFs open in the app; Word and Excel go to whichever app the
     * phone has installed. A fresh link is minted because the one in the list may
     * have expired.
     */
    private void openAttachment(com.yashsoni.skillbarter.data.model.Attachment attachment) {
        Toast.makeText(this, "Opening " + attachment.getFilename(), Toast.LENGTH_SHORT).show();
        repository.mintAttachmentLink(attachment.getId(), new SkillBarterRepository.DataCallback<String>() {
            @Override
            public void onSuccess(String url) {
                if (attachment.isPdf()) {
                    PdfViewerActivity.start(ChatActivity.this, url, attachment.getFilename());
                } else if (attachment.isImage()) {
                    Intent view = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.setDataAndType(Uri.parse(url), attachment.getMimeType());
                    view.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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

    /** Shared with the PDF viewer so it can offer the same hand-off. */
    static void openExternalFile(android.content.Context context, String url, String filename) {
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
            // Some providers refuse to describe the file; the fallback name works.
        }
        return name;
    }

    private java.io.File copyToCache(Uri uri, String name) {
        java.io.File out = new java.io.File(getCacheDir(), "upload_" + System.currentTimeMillis() + "_" + name);
        try (java.io.InputStream in = getContentResolver().openInputStream(uri);
             java.io.FileOutputStream fos = new java.io.FileOutputStream(out)) {
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
