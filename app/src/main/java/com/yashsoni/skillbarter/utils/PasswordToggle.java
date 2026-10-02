package com.yashsoni.skillbarter.utils;

import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;

import com.yashsoni.skillbarter.R;

/**
 * Wires the eye button on a password field. Tapping swaps the glyph and switches
 * between masked dots and readable text, without ever losing what was typed.
 */
public final class PasswordToggle {

    private PasswordToggle() {}

    public static void attach(final EditText field, final ImageView button) {
        if (field == null || button == null) return;

        button.setOnClickListener(v -> {
            boolean currentlyHidden = field.getInputType() ==
                (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            if (currentlyHidden) {
                field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                button.setImageResource(R.drawable.ic_eye_off);
                button.setContentDescription(field.getContext().getString(R.string.password_hide));
            } else {
                field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                button.setImageResource(R.drawable.ic_eye);
                button.setContentDescription(field.getContext().getString(R.string.password_show));
            }

            // setInputType clears the selection, which pushes the caret to the end.
            int length = field.getText() != null ? field.getText().length() : 0;
            field.setSelection(Math.min(length, field.getText() != null ? field.getText().length() : 0));
        });
    }
}