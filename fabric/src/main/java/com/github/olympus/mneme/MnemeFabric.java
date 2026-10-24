package com.github.olympus.mneme;

import com.github.olympus.mneme.client.MnemeClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;

public final class MnemeFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MnemeClient.configure(FabricLoader.getInstance().getConfigDir());
        for (KeyMapping key : MnemeClient.keys().all()) {
            KeyMappingHelper.registerKeyMapping(key);
        }
    }
}
