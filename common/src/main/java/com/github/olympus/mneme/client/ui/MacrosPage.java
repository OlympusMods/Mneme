package com.github.olympus.mneme.client.ui;

import com.github.olympus.mneme.client.Macros;
import com.github.olympus.mneme.core.macro.ChatSource;
import com.github.olympus.mneme.core.macro.Macro;
import com.github.olympus.mneme.core.macro.MacroStore;
import com.github.olympus.mneme.core.macro.Script;
import com.github.olympus.mneme.core.macro.Trigger;
import com.github.olympus.proteus.client.ui.ButtonKind;
import com.github.olympus.proteus.client.ui.FadeCanvas;
import com.github.olympus.proteus.client.ui.Gui;
import com.github.olympus.proteus.client.ui.Theme;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

final class MacrosPage {
    private static final int GAP = 6;
    private static final int ROW = 20;
    private static final int ENTRY = 32;
    private static final int ERROR = 0xFFEF4444;
    private static final String NAME = "macro-name";
    private static final String BODY = "macro-script";
    private static final String INTERVAL = "macro-interval";
    private static final String PATTERN = "macro-pattern";

    private final Gui gui;
    private final FadeCanvas fade;
    private final MacroStore store;
    private Macro current;
    private boolean capturing;

    MacrosPage(Gui gui, FadeCanvas fade, MacroStore store) {
        this.gui = gui;
        this.fade = fade;
        this.store = store;
    }

    boolean editing() {
        return current != null;
    }

    boolean capturing() {
        return capturing;
    }

    void capture(int key) {
        current.setKey(key == GLFW.GLFW_KEY_ESCAPE ? 0 : key);
        store.markDirty();
        capturing = false;
    }

    void back() {
        current = null;
        capturing = false;
        store.flush();
        gui.blur();
    }

    void render(int ix, int y, int iw, int ch) {
        if (current == null) {
            renderList(ix, y, iw, ch);
        } else {
            renderEditor(ix, y, iw, ch);
        }
    }

    private void open(Macro macro) {
        current = macro;
        capturing = false;
        gui.setField(NAME, macro.name());
        gui.setField(INTERVAL, Integer.toString(macro.interval()));
        gui.setField(PATTERN, macro.pattern());
        gui.setTextArea(BODY, macro.script());
        gui.blur();
    }

    private void renderList(int ix, int y, int iw, int ch) {
        Theme theme = gui.theme();
        if (gui.button("macro-new", ix, y, iw, ROW, text("mneme.macros.new"), ButtonKind.PRIMARY, false, true,
                text("mneme.macros.new.tooltip"))) {
            open(store.add());
            return;
        }
        y += ROW + GAP;
        int listHeight = ch - ROW - GAP;
        List<Macro> macros = store.all();
        if (macros.isEmpty()) {
            gui.centered(text("mneme.macros.empty"), ix + iw / 2, y + listHeight / 2 - 4, theme.textSecondary());
            return;
        }
        float scroll = gui.scrollBegin("macro-list", ix, y, iw, listHeight);
        int top = y - Math.round(scroll);
        int ry = top;
        int width = iw - 8;
        for (Macro macro : macros) {
            String id = "macro:" + macro.id();
            gui.rowBackground(id, ix, ry, width, ENTRY, false);
            if (gui.hit(id + "#hit", ix, ry, width, ENTRY, true, null)) {
                open(macro);
            }
            String name = macro.name().isBlank() ? text("mneme.macros.unnamed") : macro.name();
            int color = macro.enabled() ? theme.titleColor() : theme.textDisabled();
            gui.text(gui.fit(name, width - 24), ix + 10, ry + 5, color);
            gui.text(gui.fit(describe(macro), width - 24), ix + 10, ry + 17, theme.textSecondary());
            ry += ENTRY + 2;
        }
        gui.scrollEnd(ry - top);
    }

    private void renderEditor(int ix, int y, int iw, int ch) {
        int top = y;
        int back = 28;
        int run = 50;
        int delete = 62;
        if (gui.iconButton("macro-back", ix, y, back, ROW, "left", ButtonKind.NORMAL, true,
                text("mneme.macros.back"))) {
            back();
            return;
        }
        String name = gui.field(NAME, ix + back + GAP, y, iw - back - run - delete - 3 * GAP, ROW,
                text("mneme.macros.name_hint"), 40);
        if (!name.equals(current.name())) {
            current.setName(name);
            store.markDirty();
        }
        if (gui.button("macro-run", ix + iw - run - delete - GAP, y, run, ROW, text("mneme.macros.run"),
                text("mneme.macros.run.tooltip"))) {
            Macros.start(current);
        }
        if (gui.button("macro-delete", ix + iw - delete, y, delete, ROW, text("mneme.macros.delete"),
                ButtonKind.DANGER, false, true, text("mneme.macros.delete.tooltip"))) {
            store.remove(current);
            back();
            return;
        }
        y += ROW + GAP;

        int switchWidth = Math.min(130, iw / 4);
        if (gui.switchRow("macro-enabled", ix + 2, y, switchWidth, ROW, text("mneme.macros.enabled"),
                current.enabled(), true, text("mneme.macros.enabled.tooltip"), text("mneme.state.on"),
                text("mneme.state.off"))) {
            current.setEnabled(!current.enabled());
            store.markDirty();
        }
        Trigger[] triggers = Trigger.values();
        String[] labels = new String[triggers.length];
        String[] tips = new String[triggers.length];
        for (int i = 0; i < triggers.length; i++) {
            String key = triggers[i].name().toLowerCase();
            labels[i] = text("mneme.macros.trigger." + key);
            tips[i] = text("mneme.macros.trigger." + key + ".tooltip");
        }
        int segmentsX = ix + switchWidth + 18;
        int picked = gui.segmented("macro-trigger", segmentsX, y, ix + iw - segmentsX, ROW, labels,
                current.trigger().ordinal(), tips, null);
        if (picked >= 0) {
            current.setTrigger(triggers[picked]);
            capturing = false;
            store.markDirty();
        }
        y += ROW + GAP;

        int rows = renderTriggerSettings(ix, y, iw);
        y += rows * (ROW + GAP);

        Script script = Script.parse(current.script());
        int errorHeight = script.errors().isEmpty() ? 0 : 12;
        int bodyHeight = ch - (y - top) - errorHeight;
        String body = gui.textArea(BODY, ix, y, iw, bodyHeight, text("mneme.macros.script_hint"), 6000);
        if (!body.equals(current.script())) {
            current.setScript(body);
            store.markDirty();
        }
        if (errorHeight > 0) {
            gui.text(gui.fit(script.errors().get(0), iw - 4), ix + 2, y + bodyHeight + 3, ERROR);
        }
    }

    private int renderTriggerSettings(int ix, int y, int iw) {
        Theme theme = gui.theme();
        switch (current.trigger()) {
            case KEY -> {
                String label = capturing ? text("mneme.macros.key.press")
                        : current.key() == 0 ? text("mneme.macros.key.none")
                        : keyName(current.key());
                if (gui.button("macro-key", ix, y, Math.min(iw, 220), ROW, label, ButtonKind.NORMAL, capturing, true,
                        text("mneme.macros.key.tooltip"))) {
                    capturing = !capturing;
                }
            }
            case INTERVAL -> {
                gui.text(text("mneme.macros.interval"), ix + 2, gui.textY(y, ROW), theme.textSecondary());
                int labelWidth = gui.canvas().textWidth(text("mneme.macros.interval")) + 10;
                String value = gui.field(INTERVAL, ix + labelWidth, y, 70, ROW, "60", 6);
                try {
                    int seconds = Integer.parseInt(value.trim());
                    if (seconds != current.interval() && seconds > 0) {
                        current.setInterval(seconds);
                        store.markDirty();
                    }
                } catch (NumberFormatException exception) {
                    current.interval();
                }
                gui.text(text("mneme.macros.seconds"), ix + labelWidth + 78, gui.textY(y, ROW), theme.textSecondary());
            }
            case CHAT -> {
                String value = gui.field(PATTERN, ix, y, iw, ROW, text("mneme.macros.pattern_hint"), 120);
                if (!value.equals(current.pattern())) {
                    current.setPattern(value);
                    store.markDirty();
                }
                renderSources(ix, y + ROW + GAP, iw);
                return 2;
            }
            case JOIN, DEATH -> gui.text(text("mneme.macros.trigger." + current.trigger().name().toLowerCase()
                    + ".note"), ix + 2, gui.textY(y, ROW), theme.textSecondary());
        }
        return 1;
    }

    private void renderSources(int ix, int y, int iw) {
        ChatSource[] sources = ChatSource.values();
        int width = (iw - (sources.length - 1) * GAP) / sources.length;
        for (int i = 0; i < sources.length; i++) {
            ChatSource source = sources[i];
            String key = source.name().toLowerCase();
            boolean on = current.listensTo(source);
            if (gui.button("macro-source" + i, ix + i * (width + GAP), y, width, ROW,
                    text("mneme.macros.source." + key), ButtonKind.NORMAL, on, true,
                    text("mneme.macros.source." + key + ".tooltip"))) {
                current.setListensTo(source, !on);
                store.markDirty();
            }
        }
    }

    private static String describe(Macro macro) {
        String base = switch (macro.trigger()) {
            case KEY -> macro.key() == 0 ? text("mneme.macros.key.none") : keyName(macro.key());
            case JOIN -> text("mneme.macros.trigger.join.summary");
            case INTERVAL -> text("mneme.macros.trigger.interval.summary", macro.interval());
            case DEATH -> text("mneme.macros.trigger.death.summary");
            case CHAT -> text("mneme.macros.trigger.chat.summary", macro.pattern());
        };
        return macro.enabled() ? base : text("mneme.macros.disabled", base);
    }

    private static String keyName(int key) {
        return InputConstants.Type.KEYSYM.getOrCreate(key).getDisplayName().getString();
    }

    private static String text(String key, Object... arguments) {
        return Component.translatable(key, arguments).getString();
    }
}
