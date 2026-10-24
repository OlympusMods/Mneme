package com.github.olympus.mneme.core.timer;

import java.util.UUID;

public final class TimerEntry {
    private final String id = UUID.randomUUID().toString();
    private String name = "";
    private long startedAt;
    private long endsAt;
    private boolean fired;

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public long startedAt() {
        return startedAt;
    }

    public long endsAt() {
        return endsAt;
    }

    public boolean fired() {
        return fired;
    }

    void start(String label, long now, long duration) {
        name = label;
        startedAt = now;
        endsAt = now + duration;
    }

    void markFired() {
        fired = true;
    }

    public long remaining(long now) {
        return Math.max(0, endsAt - now);
    }

    public float progress(long now) {
        long total = endsAt - startedAt;
        if (total <= 0) {
            return 1f;
        }
        return Math.min(1f, Math.max(0f, (now - startedAt) / (float) total));
    }
}
