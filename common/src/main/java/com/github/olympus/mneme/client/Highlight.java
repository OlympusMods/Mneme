package com.github.olympus.mneme.client;

import com.github.olympus.mneme.core.portal.PortalMath.Point;
import com.github.olympus.mneme.core.portal.PortalPin;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;

public final class Highlight {
    private static final int INTERVAL = 8;
    private static final double RANGE = 100;
    private static final String[] COMPASS = {"E", "SE", "S", "SW", "W", "NW", "N", "NE"};

    private static int ticks;

    public record Target(String name, Point point, double distance, String heading) {
        public String describe() {
            return point.x() + " " + point.y() + " " + point.z() + "  -  " + Math.round(distance) + " blocks " + heading;
        }
    }

    private Highlight() {
    }

    public static List<Target> visible(Minecraft minecraft) {
        List<Target> targets = new ArrayList<>();
        if (minecraft.player == null || minecraft.level == null) {
            return targets;
        }
        String dimension = Compat.dimension(minecraft.level);
        for (PortalPin pin : MnemeData.portals().all()) {
            Point point = pin.shown() ? pin.in(dimension) : null;
            if (point != null) {
                double dx = point.x() + 0.5 - minecraft.player.getX();
                double dz = point.z() + 0.5 - minecraft.player.getZ();
                int slot = Math.floorMod((int) Math.round(Math.toDegrees(Math.atan2(dz, dx)) / 45.0), 8);
                targets.add(new Target(pin.name(), point, Math.sqrt(dx * dx + dz * dz), COMPASS[slot]));
            }
        }
        targets.sort(Comparator.comparingDouble(Target::distance));
        return targets;
    }

    public static void tick(Minecraft minecraft) {
        if (++ticks % INTERVAL != 0) {
            return;
        }
        for (Target target : visible(minecraft)) {
            if (target.distance() <= RANGE) {
                mark(minecraft, target.point());
            }
        }
    }

    private static void mark(Minecraft minecraft, Point point) {
        int x = point.x();
        int y = point.y();
        int z = point.z();
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
