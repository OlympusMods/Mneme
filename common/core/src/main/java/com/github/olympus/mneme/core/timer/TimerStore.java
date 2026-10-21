package com.github.olympus.mneme.core.timer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TimerStore {
    private static final Logger LOGGER = LoggerFactory.getLogger("mneme");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path file;
    private final List<TimerEntry> timers = new ArrayList<>();

    public TimerStore(Path file) {
        this.file = file;
        load();
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
        save();
        return entry;
    }

    public void remove(TimerEntry entry) {
        timers.remove(entry);
        save();
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
            save();
        }
        return due;
    }

    private void load() {
        if (!Files.isRegularFile(file)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            List<TimerEntry> read = GSON.fromJson(reader, new TypeToken<List<TimerEntry>>() { }.getType());
            if (read != null) {
                timers.addAll(read);
            }
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Could not read {}: {}", file, exception.getMessage());
        }
    }

    private void save() {
        Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                GSON.toJson(timers, writer);
            }
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            LOGGER.warn("Could not save {}: {}", file, exception.getMessage());
        }
    }
}
