package com.github.olympus.mneme.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;

public final class Highlight {
    private static final int INTERVAL = 8;
    private static final double RANGE = 100;
    private static final String[] COMPASS = {"E", "SE", "S", "SW", "W", "NW", "N", "NE"};

    private static String dimension = "";
    private static int x;
    private static int y;
    private static int z;
    private static boolean active;
    private static int ticks;

    private Highlight() {
    }

    public static synchronized void set(String targetDimension, int targetX, int targetY, int targetZ) {
        dimension = targetDimension;
        x = targetX;
        y = targetY;
        z = targetZ;
        active = true;
    }

    public static synchronized void clear() {
        active = false;
    }

    public static synchronized boolean active() {
        return active;
    }

    public static synchronized boolean visible(Minecraft minecraft) {
        return active && minecraft.player != null && minecraft.level != null
                && dimension.equals(Compat.dimension(minecraft.level));
    }

    public static synchronized String describe(Minecraft minecraft) {
        double dx = x + 0.5 - minecraft.player.getX();
        double dz = z + 0.5 - minecraft.player.getZ();
        long distance = Math.round(Math.sqrt(dx * dx + dz * dz));
        int slot = Math.floorMod((int) Math.round(Math.toDegrees(Math.atan2(dz, dx)) / 45.0), 8);
        return x + " " + y + " " + z + "  -  " + distance + " blocks " + COMPASS[slot];
    }

    public static void tick(Minecraft minecraft) {
        if (!visible(minecraft) || ++ticks % INTERVAL != 0) {
            return;
        }
        double dx = x + 0.5 - minecraft.player.getX();
        double dz = z + 0.5 - minecraft.player.getZ();
        if (dx * dx + dz * dz > RANGE * RANGE) {
            return;
        }
        for (int corner = 0; corner < 8; corner++) {
            spawn(minecraft, x + (corner & 1), y + (corner >> 1 & 1), z + (corner >> 2 & 1));
        }
        for (int edge = 0; edge < 4; edge++) {
            double px = x + (edge & 1);
            double pz = z + (edge >> 1 & 1);
            spawn(minecraft, px, y + 0.5, pz);
        }
        for (int i = 0; i < 4; i++) {
            double px = x + (i & 1);
            double py = y + (i >> 1 & 1);
            spawn(minecraft, px, py, z + 0.5);
            spawn(minecraft, x + 0.5, py, z + (i & 1));
        }
        for (double height = 2; height <= 26; height += 1.5) {
            spawn(minecraft, x + 0.5, y + height, z + 0.5);
        }
    }

    private static void spawn(Minecraft minecraft, double px, double py, double pz) {
        minecraft.level.addParticle(ParticleTypes.END_ROD, px, py, pz, 0, 0, 0);
    }
}
