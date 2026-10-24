package com.github.olympus.mneme.client.ui;

import com.github.olympus.mneme.client.GameInfo;
import com.github.olympus.mneme.client.Highlight;
import com.github.olympus.mneme.core.portal.PortalMath;
import com.github.olympus.mneme.core.portal.PortalMath.Direction;
import com.github.olympus.mneme.core.portal.PortalMath.Point;
import com.github.olympus.proteus.client.ui.FadeCanvas;
import com.github.olympus.proteus.client.ui.Gui;
import com.github.olympus.proteus.client.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

final class PortalPage {
    private static final int GAP = 6;
    private static final int ROW = 20;
    private static final String[] FIELDS = {"portal-x", "portal-y", "portal-z"};
    private static final String[] AXES = {"X", "Y", "Z"};

    private final Gui gui;
    private final FadeCanvas fade;
    private Direction direction = Direction.TO_NETHER;

    PortalPage(Gui gui, FadeCanvas fade) {
        this.gui = gui;
        this.fade = fade;
    }

    void render(int ix, int y, int iw, int ch) {
        Theme theme = gui.theme();
        String[] labels = {text("mneme.portal.to_nether"), text("mneme.portal.to_overworld")};
        String[] tips = {text("mneme.portal.to_nether.tooltip"), text("mneme.portal.to_overworld.tooltip")};
        int picked = gui.segmented("portal-direction", ix, y, iw, ROW, labels, direction.ordinal(), tips, null);
        if (picked >= 0) {
            direction = Direction.values()[picked];
        }
        y += ROW + GAP;

        int axisWidth = 14;
        int fieldWidth = (iw - 3 * axisWidth - 2 * GAP) / 3;
        Integer[] values = new Integer[3];
        for (int i = 0; i < 3; i++) {
            int fx = ix + i * (axisWidth + fieldWidth + GAP);
            gui.text(AXES[i], fx + 3, gui.textY(y, ROW), theme.textSecondary());
            values[i] = parse(gui.field(FIELDS[i], fx + axisWidth, y, fieldWidth, ROW, "0", 9));
        }
        y += ROW + GAP;

        if (gui.button("portal-here", ix, y, iw, ROW, text("mneme.portal.here"), text("mneme.portal.here.tooltip"))) {
            useMyPosition();
        }
        y += ROW + GAP;

        boolean valid = values[0] != null && values[1] != null && values[2] != null;
        Point result = valid ? PortalMath.convert(new Point(values[0], values[1], values[2]), direction) : null;
        String world = text(direction == Direction.TO_NETHER ? "mneme.portal.nether" : "mneme.portal.overworld");
        int cardHeight = 58;
        theme.card(fade, ix, y, iw, cardHeight);
        if (result == null) {
            gui.centered(text("mneme.portal.help"), ix + iw / 2, y + cardHeight / 2 - 4, theme.textSecondary());
        } else {
            String coordinates = result.x() + " " + result.y() + " " + result.z();
            gui.text(world, ix + 12, y + 8, theme.textSecondary());
            fade.push();
            fade.translate(ix + 12, y + 20);
            fade.scale(1.5f, 1.5f);
            gui.text(coordinates, 0, 0, theme.titleColor());
            fade.pop();
            gui.text(gui.fit(text("mneme.portal.range"), iw - 24), ix + 12, y + 44, theme.textSecondary());
            int copy = 60;
            if (gui.button("portal-copy", ix + iw - copy - 8, y + 6, copy, ROW - 2, text("mneme.portal.copy"),
                    text("mneme.portal.copy.tooltip"))) {
                Minecraft.getInstance().keyboardHandler.setClipboard(coordinates);
            }
        }
        y += cardHeight + GAP;

        boolean on = Highlight.active();
        String label = text("mneme.portal.highlight", world);
        if (gui.switchRow("portal-highlight", ix + 2, y, iw - 4, ROW, label, on, result != null,
                text("mneme.portal.highlight.tooltip", world), text("mneme.state.on"), text("mneme.state.off"))) {
            if (on) {
                Highlight.clear();
            } else if (result != null) {
                Highlight.set(PortalMath.targetDimension(direction), result.x(), result.y(), result.z());
            }
        }
        if (on && result != null) {
            Highlight.set(PortalMath.targetDimension(direction), result.x(), result.y(), result.z());
        }
        if (on || result != null) {
            y += ROW + GAP;
            gui.text(gui.fit(text("mneme.portal.highlight.note", world), iw - 4), ix + 2, y, theme.textSecondary());
        }
    }

    private void useMyPosition() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }
        direction = "the_nether".equals(GameInfo.dimension(minecraft)) ? Direction.TO_OVERWORLD : Direction.TO_NETHER;
        gui.setField(FIELDS[0], Integer.toString(minecraft.player.getBlockX()));
        gui.setField(FIELDS[1], Integer.toString(minecraft.player.getBlockY()));
        gui.setField(FIELDS[2], Integer.toString(minecraft.player.getBlockZ()));
    }

    private static Integer parse(String value) {
        try {
            return (int) Math.floor(Double.parseDouble(value.trim()));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static String text(String key, Object... arguments) {
        return Component.translatable(key, arguments).getString();
    }
}
