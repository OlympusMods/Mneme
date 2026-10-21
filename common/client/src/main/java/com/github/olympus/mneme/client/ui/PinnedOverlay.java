package com.github.olympus.mneme.client.ui;

import com.github.olympus.mneme.core.note.CheckItem;
import com.github.olympus.mneme.core.note.Note;
import com.github.olympus.mneme.core.note.NoteKind;
import com.github.olympus.mneme.core.note.NoteStore;
import com.github.olympus.proteus.client.ui.Canvas;
import com.github.olympus.proteus.client.ui.Draw;
import com.github.olympus.proteus.client.ui.Overlay;
import java.util.ArrayList;
import java.util.List;

public final class PinnedOverlay implements Overlay {
    private static final int MARGIN = 6;
    private static final int GAP = 4;
    private static final int PAD = 5;
    private static final int BORDER = 3;
    private static final int WIDTH = 132;
    private static final int MAX_LINES = 16;

    private final NoteStore store;

    public PinnedOverlay(NoteStore store) {
        this.store = store;
    }

    @Override
    public boolean visible() {
        return !store.pinned().isEmpty();
    }

    @Override
    public void render(Canvas canvas) {
        int[] used = {MARGIN, MARGIN};
        for (Note note : store.pinned()) {
            float scale = note.scale() / 100f;
            int side = note.side();
            List<String> lines = lines(canvas, note);
            int lineHeight = canvas.lineHeight() + 1;
            int height = PAD * 2 + lineHeight + 3 + lines.size() * lineHeight;
            int scaledWidth = Math.round(WIDTH * scale);
            int scaledHeight = Math.round(height * scale);
            int x = side == 1 ? canvas.width() - MARGIN - scaledWidth : MARGIN;
            int y = used[side];
            if (y + scaledHeight > canvas.height()) {
                continue;
            }
            draw(canvas, note, lines, x, y, height, scale, lineHeight);
            used[side] = y + scaledHeight + GAP;
        }
    }

    private void draw(Canvas canvas, Note note, List<String> lines, int x, int y, int height, float scale,
            int lineHeight) {
        int paper = NoteStyle.paper(note.material());
        int ink = NoteStyle.ink(note.material());
        canvas.push();
        canvas.translate(x, y);
        canvas.scale(scale, scale);
        Draw.shadow(canvas, 0, 0, WIDTH, height, 3, 4, 70);
        Draw.round(canvas, 0, 0, WIDTH, height, 3, NoteStyle.color(note.color()));
        Draw.round(canvas, BORDER, 0, WIDTH - BORDER, height, 3, paper);
        canvas.fill(BORDER, 0, 3, height, paper);
        String title = note.title().isBlank() ? "Untitled" : note.title();
        canvas.text(Draw.fit(canvas, title, WIDTH - BORDER - 2 * PAD), BORDER + PAD, PAD, NoteStyle.color(note.color()), false);
        int ty = PAD + lineHeight + 3;
        for (String line : lines) {
            canvas.text(line, BORDER + PAD, ty, ink, false);
            ty += lineHeight;
        }
        canvas.pop();
    }

    private static List<String> lines(Canvas canvas, Note note) {
        int width = WIDTH - BORDER - 2 * PAD;
        List<String> lines = new ArrayList<>();
        if (note.kind() == NoteKind.CHECKLIST) {
            for (CheckItem item : note.items()) {
                String mark = item.done() ? "[x] " : "[ ] ";
                List<String> wrapped = Draw.wrap(canvas, mark + item.text(), width);
                lines.addAll(wrapped.isEmpty() ? List.of(mark) : wrapped);
            }
        } else {
            for (String paragraph : note.text().split("\n", -1)) {
                List<String> wrapped = Draw.wrap(canvas, paragraph, width);
                lines.addAll(wrapped.isEmpty() ? List.of("") : wrapped);
            }
        }
        if (lines.size() > MAX_LINES) {
            List<String> cut = new ArrayList<>(lines.subList(0, MAX_LINES));
            cut.set(MAX_LINES - 1, Draw.fit(canvas, cut.get(MAX_LINES - 1) + "...", width));
            return cut;
        }
        return lines;
    }
}
