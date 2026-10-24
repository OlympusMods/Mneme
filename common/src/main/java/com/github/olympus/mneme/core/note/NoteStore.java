package com.github.olympus.mneme.core.note;

import com.github.olympus.mneme.core.JsonFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class NoteStore {
    private final JsonFile<Note> file;
    private final List<Note> notes;
    private boolean dirty;

    public NoteStore(Path path) {
        this.file = JsonFile.of(path, Note.class);
        this.notes = file.read();
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
            file.write(notes);
            dirty = false;
        }
    }
}
