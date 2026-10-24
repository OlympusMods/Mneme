package com.github.olympus.mneme;

import com.github.olympus.mneme.client.MnemeMenu;
import com.github.olympus.mneme.client.MnemeClient;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod.EventBusSubscriber(modid = Mneme.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ForgeClient {
    private ForgeClient() {
    }

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        MnemeClient.configure(FMLPaths.CONFIGDIR.get());
        MnemeMenu.register();
        for (KeyMapping key : MnemeClient.keys().all()) {
            event.register(key);
        }
    }
}
