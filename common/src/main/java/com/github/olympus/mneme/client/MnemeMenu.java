package com.github.olympus.mneme.client;

import com.github.olympus.mneme.client.ui.MnemeUi;
import com.github.olympus.proteus.client.ProteusScreen;
import com.github.olympus.proteus.client.ui.Hub;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class MnemeMenu {
    private MnemeMenu() {
    }

    public static void register() {
        Hub.register(MnemeClient.MOD_ID, () -> Component.translatable("mneme.screen.title").getString(),
                "writable_book", MnemeUi::new);
    }

    public static Screen create(Screen parent) {
        return ProteusScreen.open(parent, MnemeClient.MOD_ID);
    }
}
