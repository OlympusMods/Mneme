package com.github.olympus.mneme.mixin;

import com.github.olympus.mneme.client.MnemeClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Example mixin, kept so every loader project already has its mixin setup working. */
@Mixin(Minecraft.class)
public abstract class ClientTickMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void mneme$tick(CallbackInfo callback) {
        MnemeClient.tick((Minecraft) (Object) this);
    }
}
