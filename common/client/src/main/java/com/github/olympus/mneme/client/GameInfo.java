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
        return minecraft.player.getBlockX() + " " + minecraft.player.getBlockY() + " "
                + minecraft.player.getBlockZ() + " (" + dimension(minecraft) + ")";
    }

    public static String dimension(Minecraft minecraft) {
        return minecraft.level == null ? "" : minecraft.level.dimension().location().getPath();
    }

    public static String expand(Minecraft minecraft, String text) {
        if (minecraft.player == null) {
            return text;
        }
        return text.replace("{x}", Integer.toString(minecraft.player.getBlockX()))
                .replace("{y}", Integer.toString(minecraft.player.getBlockY()))
                .replace("{z}", Integer.toString(minecraft.player.getBlockZ()))
                .replace("{dimension}", dimension(minecraft))
                .replace("{player}", minecraft.player.getName().getString());
    }
}
