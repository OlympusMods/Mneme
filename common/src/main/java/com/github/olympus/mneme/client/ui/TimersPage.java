package com.github.olympus.mneme.client.ui;

import com.github.olympus.mneme.client.MnemeData;
import com.github.olympus.mneme.core.timer.Durations;
import com.github.olympus.mneme.core.timer.TimerEntry;
import com.github.olympus.mneme.core.timer.TimerStore;
import com.github.olympus.proteus.client.ui.ButtonKind;
import com.github.olympus.proteus.client.ui.FadeCanvas;
import com.github.olympus.proteus.client.ui.Gui;
import com.github.olympus.proteus.client.ui.Theme;
import java.util.List;
import net.minecraft.network.chat.Component;

final class TimersPage {
    private static final int GAP = 6;
    private static final int ROW = 20;
    private static final int ENTRY = 34;
    private static final int ERROR = 0xFFEF4444;
    private static final String NAME = "timer-name";
    private static final String TIME = "timer-time";
    private static final String[] PRESETS = {"1m", "5m", "10m", "30m", "1h"};

    private final Gui gui;
    private final FadeCanvas fade;
    private final TimerStore store = MnemeData.timers();
    private boolean invalid;

    TimersPage(Gui gui, FadeCanvas fade) {
        this.gui = gui;
        this.fade = fade;
    }

    void render(int ix, int y, int iw, int ch) {
        Theme theme = gui.theme();
        int top = y;
        int start = 70;
        int timeWidth = 110;
        String name = gui.field(NAME, ix, y, iw - timeWidth - start - 2 * GAP, ROW, text("mneme.timers.name_hint"), 40);
        String time = gui.field(TIME, ix + iw - timeWidth - start - GAP, y, timeWidth, ROW,
                text("mneme.timers.time_hint"), 16);
        boolean submitted = gui.submitted(TIME) | gui.submitted(NAME);
        if (gui.button("timer-start", ix + iw - start, y, start, ROW, text("mneme.timers.start"),
                ButtonKind.PRIMARY, false, true, text("mneme.timers.start.tooltip")) || submitted) {
            long duration = Durations.parse(time);
            invalid = duration <= 0;
            if (!invalid) {
                store.start(name.trim(), duration);
                gui.setField(NAME, "");
                gui.setField(TIME, "");
                gui.blur();
            }
        }
        y += ROW + GAP;

        int chip = (iw - (PRESETS.length - 1) * GAP) / PRESETS.length;
        for (int i = 0; i < PRESETS.length; i++) {
            int cx = ix + i * (chip + GAP);
            if (gui.button("timer-preset" + i, cx, y, chip, ROW, PRESETS[i], text("mneme.timers.preset.tooltip",
                    PRESETS[i]))) {
                store.start(name.trim(), Durations.parse(PRESETS[i]));
                gui.setField(NAME, "");
                invalid = false;
            }
        }
        y += ROW + GAP;
        if (invalid) {
            gui.text(text("mneme.timers.invalid"), ix + 2, y, ERROR);
            y += 12;
        }

        List<TimerEntry> timers = store.all();
        int listHeight = ch - (y - top);
        if (timers.isEmpty()) {
            gui.centered(text("mneme.timers.empty"), ix + iw / 2, y + listHeight / 2 - 4, theme.textSecondary());
            return;
        }
        long now = System.currentTimeMillis();
        float scroll = gui.scrollBegin("timer-list", ix, y, iw, listHeight);
        int ry = y - Math.round(scroll);
        int first = ry;
        int width = iw - 8;
        TimerEntry remove = null;
        for (TimerEntry entry : timers) {
            String id = "timer:" + entry.id();
            theme.card(fade, ix, ry, width, ENTRY);
            String label = entry.name().isBlank() ? text("mneme.timers.unnamed") : entry.name();
            gui.text(gui.fit(label, width - 100), ix + 10, ry + 6, theme.titleColor());
            String remaining = entry.fired() ? text("mneme.timers.finished") : Durations.format(entry.remaining(now));
            gui.right(remaining, ix + width - ROW - 14, ry + 6, entry.fired() ? theme.textSecondary() : theme.titleColor());
            gui.progress(ix + 10, ry + 22, width - 20, 5, entry.progress(now));
            if (gui.iconButton(id + "#x", ix + width - ROW - 6, ry + 4, ROW - 2, ROW - 2, "cross", ButtonKind.NORMAL,
                    true, text("mneme.timers.remove"))) {
                remove = entry;
            }
            ry += ENTRY + 4;
        }
        gui.scrollEnd(ry - first);
        if (remove != null) {
            store.remove(remove);
        }
    }

    private static String text(String key, Object... arguments) {
        return Component.translatable(key, arguments).getString();
    }
}
