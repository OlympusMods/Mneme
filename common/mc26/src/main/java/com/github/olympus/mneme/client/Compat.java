package com.github.olympus.mneme.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

public final class Compat {
    private Compat() {
    }

    public static String dimension(Level level) {
        return level.dimension().identifier().getPath();
    }

    public static Screen screen(Minecraft minecraft) {
        return minecraft.screen;
    }

    public static void show(Minecraft minecraft, Screen screen) {
        minecraft.setScreen(screen);
    }

    public static void echo(Minecraft minecraft, String text) {
        minecraft.player.sendSystemMessage(Component.literal(text));
    }

    public static boolean playSound(Minecraft minecraft, String id, float volume, float pitch) {
        Identifier location = Identifier.tryParse(id);
        if (location == null) {
            return false;
        }
        minecraft.getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(location), pitch, volume));
        return true;
    }
}
