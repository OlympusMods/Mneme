package com.github.olympus.mneme.client;

import com.github.olympus.mneme.client.ui.HighlightOverlay;
import com.github.olympus.mneme.client.ui.PinnedOverlay;
import com.github.olympus.proteus.client.ui.Overlays;
import net.minecraft.client.Minecraft;

public final class MnemeHooks {
    private static boolean installed;

    private MnemeHooks() {
    }

    public static void tick(Minecraft minecraft) {
        if (!installed) {
            installed = true;
            Overlays.add(new PinnedOverlay(MnemeData.notes()));
            Overlays.add(new HighlightOverlay());
        }
        MnemeClient.keys().poll(minecraft);
        Alarms.tick(minecraft);
        Macros.tick(minecraft);
        Highlight.tick(minecraft);
    }
}
