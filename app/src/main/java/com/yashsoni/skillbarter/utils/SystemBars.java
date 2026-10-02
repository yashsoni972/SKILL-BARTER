package com.yashsoni.skillbarter.utils;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Keeps every screen clear of the status bar, the gesture/navigation bar and the
 * software keyboard.
 *
 * The app targets SDK 35, so Android 15+ always draws edge to edge. Without this
 * the top bars slide under the status bar and the bottom navigation and chat
 * input sit under the gesture bar, which is what made the layout look broken on
 * real phones. Padding the root view once per screen fixes all of the individual
 * screens at the same time.
 */
public final class SystemBars {

    private SystemBars() {}

    /**
     * Pads the root view by the system bars and, when it is larger, by the
     * keyboard height. Using the larger of the two stops the chat input from
     * being hidden behind the keyboard.
     *
     * The original padding is captured once so repeated inset passes never
     * accumulate padding on each other.
     */
    public static void apply(View root) {
        if (root == null) return;

        final int baseLeft = root.getPaddingLeft();
        final int baseTop = root.getPaddingTop();
        final int baseRight = root.getPaddingRight();
        final int baseBottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            Insets ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime());

            v.setPadding(
                    baseLeft + bars.left,
                    baseTop + bars.top,
                    baseRight + bars.right,
                    // The keyboard already covers the navigation bar when it is
                    // open, so the larger inset wins to avoid a second gap.
                    baseBottom + Math.max(bars.bottom, ime.bottom)
            );

            // Returned unconsumed so nested scrolling and the bottom navigation
            // can still do their own inset handling.
            return windowInsets;
        });

        ViewCompat.requestApplyInsets(root);
    }
}