package com.github.olympus.mneme.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class KeyFactory {
    private static final String CATEGORY = "key.categories.mneme";

    private KeyFactory() {
    }

    public static KeyBindings create() {
        return new KeyBindings(key("menu", GLFW.GLFW_KEY_N));
    }

    private static KeyMapping key(String name, int code) {
        return new KeyMapping("key.mneme." + name, InputConstants.Type.KEYSYM, code, CATEGORY);
    }
}
