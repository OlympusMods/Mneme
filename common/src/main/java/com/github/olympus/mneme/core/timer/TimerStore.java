package com.github.olympus.mneme.core.timer;

import com.github.olympus.mneme.core.JsonFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TimerStore {
    private final JsonFile<TimerEntry> file;
    private final List<TimerEntry> timers;

    public TimerStore(Path path) {
        this.file = JsonFile.of(path, TimerEntry.class);
        this.timers = file.read();
    }

    public List<TimerEntry> all() {
        List<TimerEntry> sorted = new ArrayList<>(timers);
        sorted.sort(Comparator.comparing(TimerEntry::fired).thenComparingLong(TimerEntry::endsAt));
        return sorted;
    }

    public TimerEntry start(String name, long duration) {
        TimerEntry entry = new TimerEntry();
        entry.start(name, System.currentTimeMillis(), duration);
        timers.add(entry);
        file.write(timers);
        return entry;
    }

    public void remove(TimerEntry entry) {
        timers.remove(entry);
        file.write(timers);
    }

    public List<TimerEntry> due(long now) {
        List<TimerEntry> due = new ArrayList<>();
        for (TimerEntry entry : timers) {
            if (!entry.fired() && entry.endsAt() <= now) {
                entry.markFired();
                due.add(entry);
            }
        }
        if (!due.isEmpty()) {
            file.write(timers);
        }
        return due;
    }
}
