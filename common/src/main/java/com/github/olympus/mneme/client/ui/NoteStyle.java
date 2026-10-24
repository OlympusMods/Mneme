package com.github.olympus.mneme.client.ui;

import com.github.olympus.mneme.core.note.NoteMaterial;

final class NoteStyle {
    static final int[] COLORS = {
        0xFFEF4444, 0xFFF59E0B, 0xFFEAB308, 0xFF22C55E, 0xFF14B8A6, 0xFF3B82F6, 0xFF8B5CF6, 0xFFEC4899
    };
    static final String[] COLOR_KEYS = {"red", "orange", "yellow", "green", "teal", "blue", "violet", "pink"};

    private NoteStyle() {
    }

    static int color(int index) {
        return COLORS[Math.floorMod(index, COLORS.length)];
    }

    static int paper(NoteMaterial material) {
        return switch (material) {
            case PAPER -> 0xFFE8DDBB;
            case PLAIN -> 0xFFF5F5F2;
            case BLACK -> 0xFF15161A;
        };
    }

    static int ink(NoteMaterial material) {
        return material == NoteMaterial.BLACK ? 0xFFE6E6EA : 0xFF2B2418;
    }
}
