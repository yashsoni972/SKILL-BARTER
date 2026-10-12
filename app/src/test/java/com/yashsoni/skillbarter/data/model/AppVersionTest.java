package com.yashsoni.skillbarter.data.model;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AppVersionTest {

    private final Gson gson = new Gson();

    @Test
    public void appVersion_parsesJsonFromBackend() {
        String json = "{"
                + "\"versionCode\":2,"
                + "\"versionName\":\"1.1\","
                + "\"apkUrl\":\"https://example.com/app.apk\","
                + "\"notes\":\"New features added!\""
                + "}";

        AppVersion version = gson.fromJson(json, AppVersion.class);
        assertNotNull(version);
        assertEquals(2, version.getVersionCode());
        assertEquals("1.1", version.getVersionName());
        assertEquals("https://example.com/app.apk", version.getApkUrl());
        assertEquals("New features added!", version.getNotes());
    }

    @Test
    public void appVersion_detectsNewerVersionCode() {
        AppVersion version = new AppVersion(2, "1.1", "https://example.com/app.apk", "Update notes");
        int currentLocalVersionCode = 1;

        assertTrue("Server version code must be greater than local version code",
                version.getVersionCode() > currentLocalVersionCode);
    }
}
