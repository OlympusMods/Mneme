package com.github.olympus.mneme.client;

import com.github.olympus.mneme.core.macro.MacroStore;
import com.github.olympus.mneme.core.note.NoteStore;
import com.github.olympus.mneme.core.portal.PortalStore;
import com.github.olympus.mneme.core.timer.TimerStore;

public final class MnemeData {
    private static NoteStore notes;
    private static TimerStore timers;
    private static MacroStore macros;
    private static PortalStore portals;

    private MnemeData() {
    }

    public static synchronized MacroStore macros() {
        if (macros == null) {
            macros = new MacroStore(MnemeClient.configDirectory().resolve(MnemeClient.MOD_ID).resolve("macros.json"));
        }
        return macros;
    }

    public static synchronized TimerStore timers() {
        if (timers == null) {
            timers = new TimerStore(MnemeClient.configDirectory().resolve(MnemeClient.MOD_ID).resolve("timers.json"));
        }
        return timers;
    }

    public static synchronized PortalStore portals() {
        if (portals == null) {
            portals = new PortalStore(MnemeClient.configDirectory().resolve(MnemeClient.MOD_ID).resolve("portals.json"));
        }
        return portals;
    }

    public static synchronized NoteStore notes() {
        if (notes == null) {
            notes = new NoteStore(MnemeClient.configDirectory().resolve(MnemeClient.MOD_ID).resolve("notes.json"));
        }
        return notes;
    }
}
