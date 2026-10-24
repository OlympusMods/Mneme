package com.github.olympus.mneme;

import com.github.olympus.mneme.client.KeyFactory;
import com.github.olympus.mneme.client.MnemeMenu;
import com.github.olympus.mneme.client.MnemeClient;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(value = Mneme.MOD_ID, dist = Dist.CLIENT)
public class Mneme {
    public static final String MOD_ID = "mneme";

    public Mneme(IEventBus modBus) {
        modBus.addListener(Mneme::onRegisterKeys);
    }

    private static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        MnemeClient.configure(FMLPaths.CONFIGDIR.get());
        MnemeMenu.register();
        KeyMapping.Category category = new KeyMapping.Category(MnemeClient.id("main"));
        event.registerCategory(category);
        MnemeClient.installKeys(KeyFactory.create(category));
        for (KeyMapping key : MnemeClient.keys().all()) {
            event.register(key);
        }
    }
}
