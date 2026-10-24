package com.github.olympus.mneme;

import com.github.olympus.mneme.client.MnemeMenu;
import com.github.olympus.mneme.client.MnemeClient;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

public final class ForgeClient {
    private ForgeClient() {
    }

    static void register() {
        RegisterKeyMappingsEvent.getBus(FMLJavaModLoadingContext.get().getModBusGroup()).addListener(ForgeClient::onRegisterKeys);
    }

    private static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        MnemeClient.configure(FMLPaths.CONFIGDIR.get());
        MnemeMenu.register();
        for (KeyMapping key : MnemeClient.keys().all()) {
            event.register(key);
        }
    }
}
