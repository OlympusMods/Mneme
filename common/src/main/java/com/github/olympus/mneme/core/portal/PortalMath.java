package com.github.olympus.mneme.core.portal;

public final class PortalMath {
    public enum Direction {
        TO_NETHER,
        TO_OVERWORLD
    }

    public record Point(int x, int y, int z) {
    }

    private static final int SCALE = 8;
    private static final int NETHER_LOW = 1;
    private static final int NETHER_HIGH = 120;

    private PortalMath() {
    }

    public static Point convert(Point from, Direction direction) {
        if (direction == Direction.TO_NETHER) {
            int y = Math.max(NETHER_LOW, Math.min(NETHER_HIGH, from.y()));
            return new Point(Math.floorDiv(from.x(), SCALE), y, Math.floorDiv(from.z(), SCALE));
        }
        return new Point(from.x() * SCALE, from.y(), from.z() * SCALE);
    }
}
