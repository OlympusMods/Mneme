package com.github.olympus.mneme;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Mneme.MOD_ID)
public class Mneme {
    public static final String MOD_ID = "mneme";

    public Mneme() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ForgeClient.register();
        }
    }
}
