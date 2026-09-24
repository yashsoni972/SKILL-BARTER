package com.yashsoni.skillbarter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.yashsoni.skillbarter.data.model.User;

public class SessionManager {
    private static final String PREF_NAME = "SkillBarterSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_TOKEN = "jwtToken";
    private static final String KEY_USER = "userObj";
    private static final String KEY_ONBOARDING_COMPLETED = "onboardingCompleted";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
    }

    public void createLoginSession(String token, User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_TOKEN, token);
        if (user != null) {
            editor.putString(KEY_USER, gson.toJson(user));
        }
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, "");
    }

    public User getUser() {
        String json = pref.getString(KEY_USER, null);
        if (json != null) {
            return gson.fromJson(json, User.class);
        }
        // Default seed user fallback
        User defaultUser = new User("user_101", "Yash Soni", "yash@example.com", "Anand, Gujarat",
                "Passionate about web development, design and learning new technologies.", 4.7, 3);
        defaultUser.getOfferedSkills().add("HTML & CSS");
        defaultUser.getOfferedSkills().add("JavaScript");
        defaultUser.getOfferedSkills().add("Python");
        defaultUser.getWantedSkills().add("Graphic Design");
        defaultUser.getWantedSkills().add("UI/UX");
        defaultUser.getWantedSkills().add("Video Editing");
        return defaultUser;
    }

    public void updateUser(User user) {
        if (user != null) {
            editor.putString(KEY_USER, gson.toJson(user));
            editor.apply();
        }
    }

    public void setOnboardingCompleted(boolean completed) {
        editor.putBoolean(KEY_ONBOARDING_COMPLETED, completed);
        editor.apply();
    }

    public boolean isOnboardingCompleted() {
        return pref.getBoolean(KEY_ONBOARDING_COMPLETED, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
