package com.github.olympus.mneme.client.ui;

import com.github.olympus.mneme.client.Highlight;
import com.github.olympus.proteus.client.ui.Canvas;
import com.github.olympus.proteus.client.ui.Draw;
import com.github.olympus.proteus.client.ui.Overlay;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class HighlightOverlay implements Overlay {
    private static final int PAD = 5;

    @Override
    public boolean visible() {
        return Highlight.visible(Minecraft.getInstance());
    }

    @Override
    public void render(Canvas canvas) {
        String text = Component.translatable("mneme.portal.target", Highlight.describe(Minecraft.getInstance()))
                .getString();
        int width = canvas.textWidth(text) + 2 * PAD;
        int height = canvas.lineHeight() + 2 * PAD - 1;
        int x = (canvas.width() - width) / 2;
        int y = 6;
        Draw.panel(canvas, x, y, width, height, 4, 0xE6111218, 0x66FFFFFF);
        canvas.text(text, x + PAD, y + PAD, 0xFFFFFFFF, false);
    }
}
