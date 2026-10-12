package com.yashsoni.skillbarter.data.model;

import java.io.Serializable;

/** Holds version metadata returned by GET /api/app/version. */
public class AppVersion implements Serializable {

    private int versionCode;
    private String versionName;
    private String apkUrl;
    private String notes;

    public AppVersion() {}

    public AppVersion(int versionCode, String versionName, String apkUrl, String notes) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.apkUrl = apkUrl;
        this.notes = notes;
    }

    public int getVersionCode() { return versionCode; }
    public void setVersionCode(int versionCode) { this.versionCode = versionCode; }

    public String getVersionName() { return versionName; }
    public void setVersionName(String versionName) { this.versionName = versionName; }

    public String getApkUrl() { return apkUrl; }
    public void setApkUrl(String apkUrl) { this.apkUrl = apkUrl; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
