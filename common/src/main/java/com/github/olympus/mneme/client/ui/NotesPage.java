package com.github.olympus.mneme.client.ui;

import com.github.olympus.mneme.client.GameInfo;
import com.github.olympus.mneme.core.note.CheckItem;
import com.github.olympus.mneme.core.note.Note;
import com.github.olympus.mneme.core.note.NoteKind;
import com.github.olympus.mneme.core.note.NoteMaterial;
import com.github.olympus.mneme.core.note.NoteStore;
import com.github.olympus.proteus.client.ui.ButtonKind;
import com.github.olympus.proteus.client.ui.FadeCanvas;
import com.github.olympus.proteus.client.ui.Gui;
import com.github.olympus.proteus.client.ui.Theme;
import java.util.List;
import net.minecraft.network.chat.Component;

final class NotesPage {
    private static final int GAP = 6;
    private static final int ROW = 20;
    private static final int ENTRY = 32;
    private static final String TITLE = "note-title";
    private static final String BODY = "note-body";
    private static final String ITEM = "item:";

    private final Gui gui;
    private final FadeCanvas fade;
    private final NoteStore store;
    private Note current;

    NotesPage(Gui gui, FadeCanvas fade, NoteStore store) {
        this.gui = gui;
        this.fade = fade;
        this.store = store;
    }

    boolean editing() {
        return current != null;
    }

    void back() {
        current = null;
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

    private void open(Note note) {
        current = note;
        gui.setField(TITLE, note.title());
        gui.setTextArea(BODY, note.text());
        for (CheckItem item : note.items()) {
            gui.setField(ITEM + item.id(), item.text());
        }
        gui.blur();
    }

    private void renderList(int ix, int y, int iw, int ch) {
        Theme theme = gui.theme();
        int half = (iw - GAP) / 2;
        if (gui.button("note-new", ix, y, half, ROW, text("mneme.notes.new"), ButtonKind.PRIMARY, false, true,
                text("mneme.notes.new.tooltip"))) {
            open(store.add(NoteKind.TEXT));
            return;
        }
        if (gui.button("list-new", ix + half + GAP, y, iw - half - GAP, ROW, text("mneme.notes.new_list"),
                ButtonKind.PRIMARY, false, true, text("mneme.notes.new_list.tooltip"))) {
            open(store.add(NoteKind.CHECKLIST));
            return;
        }
        y += ROW + GAP;
        int listHeight = ch - ROW - GAP;
        List<Note> notes = store.all();
        if (notes.isEmpty()) {
            gui.centered(text("mneme.notes.empty"), ix + iw / 2, y + listHeight / 2 - 4, theme.textSecondary());
            return;
        }
        float scroll = gui.scrollBegin("note-list", ix, y, iw, listHeight);
        int top = y - Math.round(scroll);
        int width = iw - 8;
        int ry = top;
        for (Note note : notes) {
            String id = "note:" + note.id();
            gui.rowBackground(id, ix, ry, width, ENTRY, false);
            fade.fill(ix, ry, 3, ENTRY, NoteStyle.color(note.color()));
            if (gui.hit(id + "#hit", ix, ry, width, ENTRY, true, null)) {
                open(note);
            }
            String title = note.title().isBlank() ? text("mneme.notes.untitled") : note.title();
            gui.text(gui.fit(title, width - 24), ix + 12, ry + 5, theme.titleColor());
            gui.text(gui.fit(preview(note), width - 24), ix + 12, ry + 17, theme.textSecondary());
            if (note.kind() == NoteKind.CHECKLIST) {
                String progress = text("mneme.notes.progress", note.doneCount(), note.items().size());
                gui.right(progress, ix + width - 8, ry + 5, theme.textSecondary());
            }
            ry += ENTRY + 2;
        }
        gui.scrollEnd(ry - top);
    }

    private void renderEditor(int ix, int y, int iw, int ch) {
        int back = 28;
        int delete = 62;
        int coords = 70;
        if (gui.iconButton("note-back", ix, y, back, ROW, "left", ButtonKind.NORMAL, true,
                text("mneme.notes.back"))) {
            back();
            return;
        }
        String title = gui.field(TITLE, ix + back + GAP, y, iw - back - delete - coords - 3 * GAP, ROW,
                text("mneme.notes.title_hint"), 48);
        if (!title.equals(current.title())) {
            current.setTitle(title);
            store.markDirty();
        }
        if (gui.button("note-coords", ix + iw - delete - coords - GAP, y, coords, ROW, text("mneme.notes.coords"),
                text("mneme.notes.coords.tooltip"))) {
            insertCoordinates();
        }
        if (gui.button("note-delete", ix + iw - delete, y, delete, ROW, text("mneme.notes.delete"),
                ButtonKind.DANGER, false, true, text("mneme.notes.delete.tooltip"))) {
            store.remove(current);
            back();
            return;
        }
        y += ROW + GAP;
        renderStyle(ix, y, iw);
        y += ROW + GAP;
        renderPin(ix, y, iw);
        y += ROW + GAP;
        int rest = ch - 3 * (ROW + GAP);
        if (current.kind() == NoteKind.CHECKLIST) {
            renderItems(ix, y, iw, rest);
            return;
        }
        String body = gui.textArea(BODY, ix, y, iw, rest, text("mneme.notes.body_hint"), 6000);
        if (!body.equals(current.text())) {
            current.setText(body);
            store.markDirty();
        }
    }

    private void renderStyle(int ix, int y, int iw) {
        int size = 16;
        int gap = 5;
        for (int i = 0; i < NoteStyle.COLORS.length; i++) {
            int sx = ix + 2 + i * (size + gap);
            if (gui.swatch("note-color" + i, sx, y + (ROW - size) / 2, size, NoteStyle.COLORS[i],
                    current.color() == i, false, text("mneme.notes.color." + NoteStyle.COLOR_KEYS[i]))) {
                current.setColor(i);
                store.markDirty();
            }
        }
        int segmentsX = ix + 2 + NoteStyle.COLORS.length * (size + gap) + 10;
        NoteMaterial[] materials = NoteMaterial.values();
        String[] labels = new String[materials.length];
        String[] tips = new String[materials.length];
        for (int i = 0; i < materials.length; i++) {
            String key = materials[i].name().toLowerCase();
            labels[i] = text("mneme.notes.material." + key);
            tips[i] = text("mneme.notes.material." + key + ".tooltip");
        }
        int picked = gui.segmented("note-material", segmentsX, y, ix + iw - segmentsX, ROW, labels,
                current.material().ordinal(), tips, null);
        if (picked >= 0) {
            current.setMaterial(materials[picked]);
            store.markDirty();
        }
    }

    private void renderPin(int ix, int y, int iw) {
        int switchWidth = Math.min(150, iw / 3);
        if (gui.switchRow("note-pin", ix + 2, y, switchWidth, ROW, text("mneme.notes.pin"), current.pinned(), true,
                text("mneme.notes.pin.tooltip"), text("mneme.state.on"), text("mneme.state.off"))) {
            current.setPinned(!current.pinned());
            store.markDirty();
        }
        int stepperX = ix + switchWidth + 18;
        int stepperWidth = 100;
        int scale = current.scale();
        int step = gui.stepper("note-scale", stepperX, y, stepperWidth, ROW, scale + "%",
                text("mneme.notes.scale.tooltip"), scale > 50, scale < 200);
        if (step != 0) {
            current.setScale(scale + step * 10);
            store.markDirty();
        }
        int sideX = stepperX + stepperWidth + 10;
        String[] sides = {text("mneme.notes.side.left"), text("mneme.notes.side.right")};
        String[] tips = {text("mneme.notes.side.left.tooltip"), text("mneme.notes.side.right.tooltip")};
        int picked = gui.segmented("note-side", sideX, y, ix + iw - sideX, ROW, sides, current.side(), tips, null);
        if (picked >= 0) {
            current.setSide(picked);
            store.markDirty();
        }
    }

    private void renderItems(int ix, int y, int iw, int ch) {
        Theme theme = gui.theme();
        int listHeight = ch - ROW - GAP;
        float scroll = gui.scrollBegin("item-list", ix, y, iw, listHeight);
        int top = y - Math.round(scroll);
        int width = iw - 8;
        int ry = top;
        CheckItem remove = null;
        CheckItem submitted = null;
        for (CheckItem item : current.items()) {
            String id = ITEM + item.id();
            int box = 14;
            gui.checkbox(id + "#box", ix + 2, ry + (ROW - box) / 2, box, item.done(), true);
            if (gui.hit(id + "#toggle", ix, ry, box + 6, ROW, true, null)) {
                item.setDone(!item.done());
                current.changed();
                store.markDirty();
            }
            String value = gui.field(id, ix + box + 10, ry, width - box - 10 - ROW - GAP, ROW,
                    text("mneme.notes.item_hint"), 120);
            if (!value.equals(item.text())) {
                item.setText(value);
                current.changed();
                store.markDirty();
            }
            if (gui.submitted(id)) {
                submitted = item;
            }
            if (gui.iconButton("remove:" + item.id(), ix + width - ROW, ry, ROW, ROW, "cross", ButtonKind.NORMAL,
                    true, text("mneme.notes.item_remove"))) {
                remove = item;
            }
            ry += ROW + 4;
        }
        gui.scrollEnd(ry - top);
        if (remove != null) {
            current.removeItem(remove);
            store.markDirty();
        }
        if (submitted != null) {
            addItem(current.items().indexOf(submitted) + 1);
        }
        if (gui.button("item-add", ix, y + listHeight + GAP, iw, ROW, text("mneme.notes.item_add"),
                text("mneme.notes.item_add.tooltip"))) {
            addItem(current.items().size());
        }
    }

    private void insertCoordinates() {
        String coordinates = GameInfo.coordinates();
        if (coordinates == null) {
            return;
        }
        if (gui.insertAtFocus(coordinates)) {
            return;
        }
        if (current.kind() == NoteKind.CHECKLIST) {
            addItem(current.items().size());
            gui.insertAtFocus(coordinates);
        } else {
            gui.focus(BODY);
            gui.insertAtFocus(coordinates);
        }
    }

    private void addItem(int index) {
        CheckItem item = current.addItem(index);
        gui.setField(ITEM + item.id(), "");
        gui.focus(ITEM + item.id());
        store.markDirty();
    }

    private static String preview(Note note) {
        if (note.kind() == NoteKind.CHECKLIST) {
            return note.items().stream().filter(item -> !item.done()).map(CheckItem::text)
                    .filter(value -> !value.isBlank()).findFirst()
                    .orElse(Component.translatable("mneme.notes.no_text").getString());
        }
        String line = note.text().lines().findFirst().orElse("");
        return line.isBlank() ? Component.translatable("mneme.notes.no_text").getString() : line;
    }

    private static String text(String key, Object... arguments) {
        return Component.translatable(key, arguments).getString();
    }
}
