package com.github.olympus.mneme;

import com.github.olympus.mneme.client.MnemeClient;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(Mneme.MOD_ID)
public class Mneme {
    public static final String MOD_ID = "mneme";

    public Mneme(IEventBus modBus) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener(Mneme::onRegisterKeys);
        }
    }

    private static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        MnemeClient.configure(FMLPaths.CONFIGDIR.get());
        for (KeyMapping key : MnemeClient.keys().all()) {
            event.register(key);
        }
    }
}
