package com.github.olympus.mneme.mixin;

import com.github.olympus.mneme.client.Macros;
import com.github.olympus.mneme.core.macro.ChatSource;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class SendMixin {
    @Inject(method = "sendChat(Ljava/lang/String;)V", at = @At("HEAD"))
    private void mneme$sendChat(String message, CallbackInfo callback) {
        Macros.chat(message, ChatSource.SENT);
    }

    @Inject(method = "sendCommand(Ljava/lang/String;)V", at = @At("HEAD"))
    private void mneme$sendCommand(String command, CallbackInfo callback) {
        Macros.chat("/" + command, ChatSource.COMMAND);
    }
}
