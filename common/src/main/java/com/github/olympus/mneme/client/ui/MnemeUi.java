package com.github.olympus.mneme.client.ui;

import com.github.olympus.mneme.client.MnemeData;
import com.github.olympus.proteus.client.ui.ButtonKind;
import com.github.olympus.proteus.client.ui.Theme;
import com.github.olympus.proteus.client.ui.UiHost;
import com.github.olympus.proteus.client.ui.UiShell;
import net.minecraft.network.chat.Component;

public final class MnemeUi extends UiShell {
    private static final int PAD = 10;
    private static final int GAP = 6;
    private static final int ROW = 20;

    private enum Tab {
        NOTES,
        TIMERS,
        CALCULATOR,
        MACROS
    }

    private final NotesPage notes;
    private final TimersPage timers;
    private final CalculatorPage calculator;
    private final MacrosPage macros;
    private Tab tab = Tab.NOTES;

    public MnemeUi(UiHost host) {
        super(host);
        this.notes = new NotesPage(gui(), fade(), MnemeData.notes());
        this.timers = new TimersPage(gui(), fade());
        this.calculator = new CalculatorPage(gui(), fade());
        this.macros = new MacrosPage(gui(), fade(), MnemeData.macros());
    }

    @Override
    protected boolean onEscape() {
        if (tab == Tab.NOTES && notes.editing()) {
            notes.back();
            return true;
        }
        if (tab == Tab.MACROS && macros.editing()) {
            macros.back();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int key, int mods) {
        if (tab == Tab.MACROS && macros.capturing()) {
            macros.capture(key);
            return true;
        }
        return super.keyPressed(key, mods);
    }

    @Override
    protected void onTick() {
        MnemeData.notes().flush();
        MnemeData.macros().flush();
    }

    @Override
    public void removed() {
        MnemeData.notes().flush();
        MnemeData.macros().flush();
    }

    @Override
    protected void renderContent(Theme theme) {
        int pw = windowWidth();
        int ph = windowHeight();
        int px = windowX(pw);
        int py = windowY(ph);
        theme.window(fade(), px, py, pw, ph);

        int ix = px + PAD;
        int iw = pw - 2 * PAD;
        int y = py + PAD;

        String title = text("mneme.screen.title");
        if (theme.centeredTitle() && pw >= 340) {
            gui().centered(title, px + pw / 2, y + 5, theme.titleColor());
        } else {
            fade().push();
            fade().translate(ix, y + 2);
            fade().scale(1.25f, 1.25f);
            gui().text(title, 0, 0, theme.titleColor());
            fade().pop();
        }
        y += 26;

        String[] tabs = {
            text("mneme.tab.notes"), text("mneme.tab.timers"), text("mneme.tab.calculator"),
            text("mneme.tab.macros")
        };
        String[] tips = {
            text("mneme.tab.notes.tooltip"), text("mneme.tab.timers.tooltip"), text("mneme.tab.calculator.tooltip"),
            text("mneme.tab.macros.tooltip")
        };
        int chosen = gui().segmented("tabs", ix, y, iw, 20, tabs, tab.ordinal(), tips, null);
        if (chosen >= 0) {
            tab = Tab.values()[chosen];
            restartContentFade();
            if (tab == Tab.CALCULATOR) {
                calculator.opened();
            }
        }
        y += 20 + GAP;

        int footerY = py + ph - PAD - ROW;
        int contentHeight = footerY - GAP - y;

        float before = beginContent();
        switch (tab) {
            case NOTES -> notes.render(ix, y, iw, contentHeight);
            case TIMERS -> timers.render(ix, y, iw, contentHeight);
            case CALCULATOR -> calculator.render(ix, y, iw, contentHeight);
            case MACROS -> macros.render(ix, y, iw, contentHeight);
        }
        endContent(before);

        if (gui().button("done", ix, footerY, iw, ROW, text("mneme.screen.done"), ButtonKind.PRIMARY, false, true,
                null)) {
            requestClose();
        }
    }

    private static String text(String key, Object... arguments) {
        return Component.translatable(key, arguments).getString();
    }
}
