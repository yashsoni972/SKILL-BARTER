package com.yashsoni.skillbarter.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Turns the ISO-8601 timestamps Mongo returns into short human labels
 * ("Just now", "3h ago", "27 Sep 2026"). Falls back to the raw string when the
 * value is not a timestamp, so an unexpected format still shows something.
 */
public final class TimeFormat {

    private TimeFormat() {}

    public static String relative(String isoTimestamp) {
        if (isoTimestamp == null || isoTimestamp.trim().isEmpty()) {
            return "";
        }

        Date date = parse(isoTimestamp);
        if (date == null) {
            return isoTimestamp;
        }

        long diffMillis = System.currentTimeMillis() - date.getTime();
        if (diffMillis < 0) diffMillis = 0;

        long minutes = diffMillis / 60000L;
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + "m ago";

        long hours = minutes / 60L;
        if (hours < 24) return hours + "h ago";

        long days = hours / 24L;
        if (days < 7) return days + "d ago";

        return format(date, "d MMM yyyy");
    }

    /** "27 Sep 2026" for dates stored as "2026-09-27". */
    public static String date(String isoDate) {
        if (isoDate == null || isoDate.trim().isEmpty()) {
            return "";
        }
        Date date = parse(isoDate);
        return date == null ? isoDate : format(date, "d MMM yyyy");
    }

    private static String format(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    private static Date parse(String value) {
        // Zoned (what Mongo sends) first, then the same shapes without a zone.
        String[] patterns = {
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
        };

        for (String pattern : patterns) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
                format.setLenient(false);
                if (pattern.endsWith("'Z'")) {
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                } else {
                    format.setTimeZone(TimeZone.getDefault());
                }
                return format.parse(value);
            } catch (Exception ignored) {
                // Try the next pattern.
            }
        }
        return null;
    }
}