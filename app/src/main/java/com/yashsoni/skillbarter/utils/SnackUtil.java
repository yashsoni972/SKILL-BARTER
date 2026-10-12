package com.yashsoni.skillbarter.utils;

import android.app.Activity;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

/** Replaces Toast with a fast, professional Snackbar. */
public class SnackUtil {

    public static void show(View root, String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showLong(View root, String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }

    public static void show(Activity activity, String message) {
        View root = activity.findViewById(android.R.id.content);
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showLong(Activity activity, String message) {
        View root = activity.findViewById(android.R.id.content);
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }

    public static void show(Fragment fragment, String message) {
        if (fragment.getView() != null)
            Snackbar.make(fragment.getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showLong(Fragment fragment, String message) {
        if (fragment.getView() != null)
            Snackbar.make(fragment.getView(), message, Snackbar.LENGTH_LONG).show();
    }
}
