package com.github.olympus.mneme.mixin;

import com.github.olympus.mneme.client.Macros;
import com.github.olympus.mneme.core.macro.ChatSource;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public abstract class ChatMixin {
    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"))
    private void mneme$onMessage(Component message, MessageSignature signature, GuiMessageTag tag,
            CallbackInfo callback) {
        Macros.chat(message.getString(), ChatSource.RECEIVED);
    }
}
