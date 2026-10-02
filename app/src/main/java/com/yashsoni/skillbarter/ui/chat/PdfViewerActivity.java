package com.yashsoni.skillbarter.ui.chat;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.yashsoni.skillbarter.R;
import com.yashsoni.skillbarter.databinding.ActivityPdfViewerBinding;
import com.yashsoni.skillbarter.utils.SystemBars;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders a shared PDF in-app using Android's built-in PdfRenderer, so a PDF can
 * be read without any external app installed.
 */
public class PdfViewerActivity extends AppCompatActivity {

    private static final String EXTRA_URL = "url";
    private static final String EXTRA_TITLE = "title";
    private static final int MAX_BITMAP_WIDTH = 1400;

    private ActivityPdfViewerBinding binding;
    private ParcelFileDescriptor descriptor;
    private PdfRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.apply(binding.getRoot());

        String url = getIntent().getStringExtra(EXTRA_URL);
        String title = getIntent().getStringExtra(EXTRA_TITLE);

        binding.tvPdfTitle.setText(title == null ? "Document" : title);
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnOpenElsewhere.setOnClickListener(v -> {
            if (url != null) ChatActivity.openExternalFile(this, url, title == null ? "document.pdf" : title);
        });

        if (url == null) {
            fail();
            return;
        }

        binding.progress.setVisibility(View.VISIBLE);
        new Thread(() -> {
            File cached = null;
            try {
                cached = download(url);
                final File file = cached;
                runOnUiThread(() -> render(file));
            } catch (Exception e) {
                runOnUiThread(this::fail);
            }
        }, "pdf-download").start();
    }

    /** The signed link is short lived, so the bytes are cached for this session. */
    private File download(String url) throws Exception {
        File dir = new File(getCacheDir(), "pdf");
        if (!dir.exists() && !dir.mkdirs()) throw new IllegalStateException("cache unavailable");

        String name = url.contains("?") ? url.substring(0, url.indexOf('?')) : url;
        File out = new File(dir, Integer.toHexString(name.hashCode()) + ".pdf");

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(20000);
        conn.setReadTimeout(30000);
        try (InputStream in = conn.getInputStream(); FileOutputStream fos = new FileOutputStream(out)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) fos.write(buffer, 0, read);
        } finally {
            conn.disconnect();
        }
        return out;
    }

    private void render(File file) {
        try {
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            renderer = new PdfRenderer(descriptor);

            final List<PdfRenderer.Page> pages = new ArrayList<>();
            for (int i = 0; i < renderer.getPageCount(); i++) pages.add(renderer.openPage(i));

            binding.progress.setVisibility(View.GONE);
            binding.pager.setVisibility(View.VISIBLE);
            binding.pager.setAdapter(new PageAdapter(pages));
            binding.tvPageCount.setText(pages.size() + (pages.size() == 1 ? " page" : " pages"));
        } catch (Exception e) {
            fail();
        }
    }

    private void fail() {
        binding.progress.setVisibility(View.GONE);
        binding.pager.setVisibility(View.GONE);
        binding.tvError.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (renderer != null) renderer.close();
        if (descriptor != null) {
            try {
                descriptor.close();
            } catch (Exception ignored) {
                // The descriptor is already released; nothing useful to do.
            }
        }
    }

    private static class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageHolder> {
        private final List<PdfRenderer.Page> pages;

        PageAdapter(List<PdfRenderer.Page> pages) {
            this.pages = pages;
        }

        @NonNull
        @Override
        public PageHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pdf_page, parent, false);
            return new PageHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PageHolder holder, int position) {
            PdfRenderer.Page page = pages.get(position);
            try {
                int width = MAX_BITMAP_WIDTH;
                int height = width * page.getHeight() / Math.max(page.getWidth(), 1);

                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(Color.WHITE);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                holder.image.setImageBitmap(bitmap);
            } catch (Exception e) {
                holder.image.setImageDrawable(null);
            } finally {
                page.close();
            }
        }

        @Override
        public int getItemCount() {
            return pages.size();
        }

        static class PageHolder extends RecyclerView.ViewHolder {
            final android.widget.ImageView image;

            PageHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.ivPage);
            }
        }
    }

    static void start(android.content.Context context, String url, String title) {
        android.content.Intent i = new android.content.Intent(context, PdfViewerActivity.class);
        i.putExtra(EXTRA_URL, url);
        i.putExtra(EXTRA_TITLE, title);
        context.startActivity(i);
    }
}