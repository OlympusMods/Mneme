package com.github.olympus.mneme.core.timer;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Durations {
    private static final Pattern PART = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(hours?|hrs?|h|minutes?|mins?|m|seconds?|secs?|s)?");
    private static final Pattern CLOCK = Pattern.compile("(\\d+):(\\d{1,2})(?::(\\d{1,2}))?");

    private Durations() {
    }

    public static long parse(String input) {
        String text = input.trim().toLowerCase(Locale.ROOT);
        if (text.isEmpty()) {
            return -1;
        }
        Matcher clock = CLOCK.matcher(text);
        if (clock.matches()) {
            long first = Long.parseLong(clock.group(1));
            long second = Long.parseLong(clock.group(2));
            if (clock.group(3) != null) {
                return (first * 3600 + second * 60 + Long.parseLong(clock.group(3))) * 1000;
            }
            return (first * 60 + second) * 1000;
        }
        Matcher matcher = PART.matcher(text);
        double seconds = 0;
        int end = 0;
        boolean any = false;
        while (matcher.find()) {
            if (!text.substring(end, matcher.start()).isBlank()) {
                return -1;
            }
            double amount = Double.parseDouble(matcher.group(1));
            String unit = matcher.group(2) == null ? "m" : matcher.group(2);
            seconds += switch (unit.charAt(0)) {
                case 'h' -> amount * 3600;
                case 's' -> amount;
                default -> amount * 60;
            };
            end = matcher.end();
            any = true;
        }
        if (!any || !text.substring(end).isBlank()) {
            return -1;
        }
        return Math.round(seconds * 1000);
    }

    public static String format(long millis) {
        long total = Math.max(0, (millis + 999) / 1000);
        long hours = total / 3600;
        long minutes = total % 3600 / 60;
        long seconds = total % 60;
        if (hours > 0) {
            return String.format(Locale.ROOT, "%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format(Locale.ROOT, "%d:%02d", minutes, seconds);
    }
}
