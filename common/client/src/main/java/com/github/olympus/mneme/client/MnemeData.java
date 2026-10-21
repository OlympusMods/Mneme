package com.github.olympus.mneme.client;

import com.github.olympus.mneme.core.note.NoteStore;

public final class MnemeData {
    private static NoteStore notes;

    private MnemeData() {
    }

    public static synchronized NoteStore notes() {
        if (notes == null) {
            notes = new NoteStore(MnemeClient.configDirectory().resolve(MnemeClient.MOD_ID).resolve("notes.json"));
        }
        return notes;
    }
}
