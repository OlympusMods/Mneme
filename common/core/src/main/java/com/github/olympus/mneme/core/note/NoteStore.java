package com.github.olympus.mneme.core.note;

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

public final class NoteStore {
    private static final Logger LOGGER = LoggerFactory.getLogger("mneme");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path file;
    private final List<Note> notes = new ArrayList<>();
    private boolean dirty;

    public NoteStore(Path file) {
        this.file = file;
        load();
    }

    public List<Note> all() {
        List<Note> sorted = new ArrayList<>(notes);
        sorted.sort(Comparator.comparingLong(Note::updated).reversed());
        return sorted;
    }

    public List<Note> pinned() {
        List<Note> pinned = new ArrayList<>();
        for (Note note : notes) {
            if (note.pinned()) {
                pinned.add(note);
            }
        }
        return pinned;
    }

    public Note add(NoteKind kind) {
        Note note = new Note();
        note.setKind(kind);
        notes.add(note);
        dirty = true;
        return note;
    }

    public void remove(Note note) {
        notes.remove(note);
        dirty = true;
    }

    public void markDirty() {
        dirty = true;
    }

    public void flush() {
        if (dirty) {
            save();
        }
    }

    private void load() {
        if (!Files.isRegularFile(file)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            List<Note> read = GSON.fromJson(reader, new TypeToken<List<Note>>() { }.getType());
            if (read != null) {
                notes.addAll(read);
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
                GSON.toJson(notes, writer);
            }
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            dirty = false;
        } catch (IOException exception) {
            LOGGER.warn("Could not save {}: {}", file, exception.getMessage());
        }
    }
}
