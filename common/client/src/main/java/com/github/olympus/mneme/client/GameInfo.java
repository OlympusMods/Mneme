package com.github.olympus.mneme.client;

import net.minecraft.client.Minecraft;

public final class GameInfo {
    private GameInfo() {
    }

    public static String coordinates() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return null;
        }
        String dimension = minecraft.level.dimension().location().getPath();
        return minecraft.player.getBlockX() + " " + minecraft.player.getBlockY() + " "
                + minecraft.player.getBlockZ() + " (" + dimension + ")";
    }
}
