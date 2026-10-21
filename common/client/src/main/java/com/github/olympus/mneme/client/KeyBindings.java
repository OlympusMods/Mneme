package com.github.olympus.mneme.client;

import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public final class KeyBindings {
    private final KeyMapping menu;

    public KeyBindings(KeyMapping menu) {
        this.menu = menu;
    }

    public List<KeyMapping> all() {
        return List.of(menu);
    }

    public void poll(Minecraft minecraft) {
        while (menu.consumeClick()) {
            Compat.show(minecraft, MnemeMenu.create(Compat.screen(minecraft)));
        }
    }
}
