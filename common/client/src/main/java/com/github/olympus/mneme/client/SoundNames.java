package com.github.olympus.mneme.client;

import java.util.Locale;
import java.util.Map;

public final class SoundNames {
    private static final Map<String, String> ALIASES = Map.ofEntries(
            Map.entry("bell", "block.note_block.bell"),
            Map.entry("ding", "block.note_block.bell"),
            Map.entry("pling", "block.note_block.pling"),
            Map.entry("chime", "block.note_block.chime"),
            Map.entry("bass", "block.note_block.bass"),
            Map.entry("levelup", "entity.player.levelup"),
            Map.entry("xp", "entity.experience_orb.pickup"),
            Map.entry("anvil", "block.anvil.land"),
            Map.entry("click", "ui.button.click"),
            Map.entry("alarm", "block.bell.use"),
            Map.entry("horn", "event.raid.horn"));

    private SoundNames() {
    }

    public static String resolve(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        if (lower.indexOf(':') >= 0) {
            return lower;
        }
        return "minecraft:" + ALIASES.getOrDefault(lower, lower);
    }
}
