package com.github.olympus.mneme.client.ui;

import com.github.olympus.mneme.core.calc.Calculator;
import com.github.olympus.mneme.core.calc.Calculator.CalcException;
import com.github.olympus.proteus.client.ui.ButtonKind;
import com.github.olympus.proteus.client.ui.FadeCanvas;
import com.github.olympus.proteus.client.ui.Gui;
import com.github.olympus.proteus.client.ui.Theme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;

final class CalculatorPage {
    private record Entry(String expression, double value) {
    }

    private static final List<Entry> HISTORY = new ArrayList<>();
    private static final int GAP = 6;
    private static final int ROW = 20;
    private static final int ENTRY = 24;
    private static final int ERROR = 0xFFEF4444;
    private static final String INPUT = "calc-input";

    private final Gui gui;
    private final FadeCanvas fade;
    private final PortalPage portal;
    private int mode;

    CalculatorPage(Gui gui, FadeCanvas fade) {
        this.gui = gui;
        this.fade = fade;
        this.portal = new PortalPage(gui, fade);
    }

    void opened() {
        if (mode == 0) {
            gui.focus(INPUT);
        }
    }

    void render(int ix, int y, int iw, int ch) {
        Theme theme = gui.theme();
        String[] modes = {text("mneme.calc.mode.math"), text("mneme.calc.mode.portal")};
        String[] modeTips = {text("mneme.calc.mode.math.tooltip"), text("mneme.calc.mode.portal.tooltip")};
        int picked = gui.segmented("calc-mode", ix, y, iw, ROW, modes, mode, modeTips, null);
        if (picked >= 0) {
            mode = picked;
            gui.blur();
        }
        y += ROW + GAP;
        ch -= ROW + GAP;
        if (mode == 1) {
            portal.render(ix, y, iw, ch);
            return;
        }
        int top = y;
        double previous = HISTORY.isEmpty() ? 0 : HISTORY.get(0).value();
        String input = gui.field(INPUT, ix, y, iw, ROW + 4, text("mneme.calc.hint"), 120);
        y += ROW + 4 + GAP;

        String shown = "";
        String detail = "";
        boolean valid = false;
        double value = 0;
        if (!input.isBlank()) {
            try {
                value = Calculator.evaluate(input, previous);
                valid = true;
                shown = "= " + Calculator.format(value);
                detail = Calculator.stacks(value);
            } catch (CalcException exception) {
                detail = exception.getMessage();
            }
        }
        int cardHeight = 44;
        theme.card(fade, ix, y, iw, cardHeight);
        if (input.isBlank()) {
            gui.centered(text("mneme.calc.help"), ix + iw / 2, y + cardHeight / 2 - 4, theme.textSecondary());
        } else {
            fade.push();
            fade.translate(ix + 12, y + 8);
            fade.scale(1.5f, 1.5f);
            gui.text(shown, 0, 0, valid ? theme.titleColor() : theme.textDisabled());
            fade.pop();
            gui.text(gui.fit(detail, iw - 24), ix + 12, y + 30, valid ? theme.textSecondary() : ERROR);
        }
        y += cardHeight + GAP;

        if (valid && gui.submitted(INPUT)) {
            HISTORY.add(0, new Entry(input.trim(), value));
            gui.setField(INPUT, "");
            gui.focus(INPUT);
        }

        int clear = 70;
        gui.text(text("mneme.calc.history"), ix + 2, y + 5, theme.textSecondary());
        if (gui.button("calc-clear", ix + iw - clear, y, clear, ROW, text("mneme.calc.clear"), ButtonKind.NORMAL,
                false, !HISTORY.isEmpty(), text("mneme.calc.clear.tooltip"))) {
            HISTORY.clear();
        }
        y += ROW + GAP;
        int listHeight = ch - (y - top);
        if (HISTORY.isEmpty()) {
            return;
        }
        float scroll = gui.scrollBegin("calc-history", ix, y, iw, listHeight);
        int ry = y - Math.round(scroll);
        int start = ry;
        int width = iw - 8;
        Entry reuse = null;
        for (int i = 0; i < HISTORY.size(); i++) {
            Entry entry = HISTORY.get(i);
            String id = "calc-entry" + i;
            gui.rowBackground(id, ix, ry, width, ENTRY, false);
            if (gui.hit(id + "#hit", ix, ry, width, ENTRY, true, text("mneme.calc.reuse"))) {
                reuse = entry;
            }
            String result = "= " + Calculator.format(entry.value());
            String breakdown = Calculator.stacks(entry.value());
            if (!breakdown.isEmpty()) {
                result += "  (" + breakdown + ")";
            }
            gui.right(gui.fit(result, width / 2), ix + width - 8, gui.textY(ry, ENTRY), theme.titleColor());
            gui.text(gui.fit(entry.expression(), width / 2 - 16), ix + 8, gui.textY(ry, ENTRY), theme.textSecondary());
            ry += ENTRY + 2;
        }
        gui.scrollEnd(ry - start);
        if (reuse != null) {
            gui.setField(INPUT, reuse.expression());
            gui.focus(INPUT);
        }
    }

    private static String text(String key, Object... arguments) {
        return Component.translatable(key, arguments).getString();
    }
}
