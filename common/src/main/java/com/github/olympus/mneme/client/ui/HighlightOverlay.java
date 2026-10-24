package com.github.olympus.mneme.client.ui;

import com.github.olympus.mneme.client.Highlight;
import com.github.olympus.mneme.client.Highlight.Target;
import com.github.olympus.proteus.client.ui.Canvas;
import com.github.olympus.proteus.client.ui.Draw;
import com.github.olympus.proteus.client.ui.Overlay;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class HighlightOverlay implements Overlay {
    private static final int PAD = 5;
    private static final int LIMIT = 4;

    @Override
    public boolean visible() {
        return !Highlight.visible(Minecraft.getInstance()).isEmpty();
    }

    @Override
    public void render(Canvas canvas) {
        List<String> lines = new ArrayList<>();
        int width = 0;
        for (Target target : Highlight.visible(Minecraft.getInstance())) {
            if (lines.size() == LIMIT) {
                break;
            }
            String name = target.name().isBlank() ? text("mneme.portal.unnamed") : target.name();
            String line = text("mneme.portal.target", name, target.describe());
            lines.add(line);
            width = Math.max(width, canvas.textWidth(line));
        }
        if (lines.isEmpty()) {
            return;
        }
        int step = canvas.lineHeight() + 2;
        width += 2 * PAD;
        int height = lines.size() * step - 2 + 2 * PAD - 1;
        int x = (canvas.width() - width) / 2;
        int y = 6;
        Draw.panel(canvas, x, y, width, height, 4, 0xE6111218, 0x66FFFFFF);
        for (int i = 0; i < lines.size(); i++) {
            canvas.text(lines.get(i), x + PAD, y + PAD + i * step, 0xFFFFFFFF, false);
        }
    }

    private static String text(String key, Object... arguments) {
        return Component.translatable(key, arguments).getString();
    }
}
