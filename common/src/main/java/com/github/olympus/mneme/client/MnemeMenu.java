package com.github.olympus.mneme.client;

import com.github.olympus.mneme.client.ui.MnemeUi;
import com.github.olympus.proteus.client.ProteusScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class MnemeMenu {
    private MnemeMenu() {
    }

    public static Screen create(Screen parent) {
        return new ProteusScreen(Component.translatable("mneme.screen.title"), parent, MnemeUi::new);
    }
}
