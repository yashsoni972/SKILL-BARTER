package com.yashsoni.skillbarter.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** Metadata for a file shared in a chat. The bytes live on the server. */
public class Attachment implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("filename")
    private String filename;
    @SerializedName("mimeType")
    private String mimeType;
    @SerializedName("size")
    private long size;
    @SerializedName("downloadUrl")
    private String downloadUrl;

    public Attachment() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    /** True for the formats we render inside the app instead of handing off. */
    public boolean isImage() {
        return mimeType != null && (mimeType.startsWith("image/"));
    }

    public boolean isPdf() {
        return "application/pdf".equalsIgnoreCase(mimeType);
    }

    /** e.g. 1.4 MB, shown next to the file name. */
    public String humanSize() {
        if (size <= 0) return "";
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return (size / 1024) + " KB";
        return String.format(java.util.Locale.getDefault(), "%.1f MB", size / (1024.0 * 1024.0));
    }
}