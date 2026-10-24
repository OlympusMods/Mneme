package com.github.olympus.mneme.client;

import com.github.olympus.mneme.core.timer.TimerEntry;
import com.github.olympus.proteus.client.ui.ToastOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public final class Alarms {
    private static final int CHECK_TICKS = 10;

    private static int ticks;

    private Alarms() {
    }

    public static void tick(Minecraft minecraft) {
        if (++ticks % CHECK_TICKS != 0) {
            return;
        }
        for (TimerEntry entry : MnemeData.timers().due(System.currentTimeMillis())) {
            ring(minecraft, entry);
        }
    }

    private static void ring(Minecraft minecraft, TimerEntry entry) {
        String name = entry.name().isBlank() ? Component.translatable("mneme.timers.unnamed").getString() : entry.name();
        ToastOverlay.show(Component.translatable("mneme.timers.done").getString(), name, "clock");
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BELL, 1.0F));
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BELL, 1.5F));
    }
}
