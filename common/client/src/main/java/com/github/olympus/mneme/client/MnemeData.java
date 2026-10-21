package com.github.olympus.mneme.client;

import com.github.olympus.mneme.core.note.NoteStore;
import com.github.olympus.mneme.core.timer.TimerStore;

public final class MnemeData {
    private static NoteStore notes;
    private static TimerStore timers;

    private MnemeData() {
    }

    public static synchronized TimerStore timers() {
        if (timers == null) {
            timers = new TimerStore(MnemeClient.configDirectory().resolve(MnemeClient.MOD_ID).resolve("timers.json"));
        }
        return timers;
    }

    public static synchronized NoteStore notes() {
        if (notes == null) {
            notes = new NoteStore(MnemeClient.configDirectory().resolve(MnemeClient.MOD_ID).resolve("notes.json"));
        }
        return notes;
    }
}
