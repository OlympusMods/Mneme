package com.github.olympus.mneme.core.portal;

import com.github.olympus.mneme.core.portal.PortalMath.Point;
import java.util.UUID;

public final class PortalPin {
    private final String id = UUID.randomUUID().toString();
    private String name = "";
    private int overworldX;
    private int overworldY;
    private int overworldZ;
    private int netherX;
    private int netherY;
    private int netherZ;
    private boolean shown = true;

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public boolean shown() {
        return shown;
    }

    public Point overworld() {
        return new Point(overworldX, overworldY, overworldZ);
    }

    public Point nether() {
        return new Point(netherX, netherY, netherZ);
    }

    public Point in(String dimension) {
        if ("overworld".equals(dimension)) {
            return overworld();
        }
        if ("the_nether".equals(dimension)) {
            return nether();
        }
        return null;
    }

    void place(String label, Point overworld, Point nether) {
        name = label;
        overworldX = overworld.x();
        overworldY = overworld.y();
        overworldZ = overworld.z();
        netherX = nether.x();
        netherY = nether.y();
        netherZ = nether.z();
    }

    void show(boolean value) {
        shown = value;
    }
}
