package com.github.olympus.mneme;

import com.github.olympus.mneme.client.MnemeMenu;
import com.github.olympus.mneme.client.MnemeClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;

public final class MnemeFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MnemeClient.configure(FabricLoader.getInstance().getConfigDir());
        MnemeMenu.register();
        for (KeyMapping key : MnemeClient.keys().all()) {
            KeyBindingHelper.registerKeyBinding(key);
        }
    }
}
