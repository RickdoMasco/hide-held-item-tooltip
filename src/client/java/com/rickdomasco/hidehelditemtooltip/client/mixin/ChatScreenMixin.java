package com.rickdomasco.hidehelditemtooltip.client.mixin;

import com.rickdomasco.hidehelditemtooltip.client.HHITModClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;

import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
    private void handleHhitCommand(@NonNull String msg, boolean addToRecent, CallbackInfo info) {
        String normalized = ((ChatScreen) (Object) this).normalizeChatMessage(msg);
        if (normalized.isEmpty()) {
            return;
        }

        String[] parts = normalized.split(" ", 2);
        if (!parts[0].equalsIgnoreCase("/hhit")) {
            return;
        }

        if (addToRecent) {
            Minecraft.getInstance().gui.getChat().addRecentChat(normalized);
        }

        String argument = parts.length > 1 ? parts[1].trim() : "";
        boolean enabled;
        String statusMessage;

        if (argument.isEmpty()) {
            enabled = HHITModClient.toggleHideHeldItemTooltip();
            statusMessage = enabled ? "HHIT: tooltip hidden" : "HHIT: tooltip visible";
        } else if (argument.equalsIgnoreCase("on")) {
            HHITModClient.setHideHeldItemTooltipEnabled(true);
            enabled = true;
            statusMessage = "HHIT: tooltip hidden";
        } else if (argument.equalsIgnoreCase("off")) {
            HHITModClient.setHideHeldItemTooltipEnabled(false);
            enabled = false;
            statusMessage = "HHIT: tooltip visible";
        } else {
            showHhitFeedback("HHIT: use /hhit to swap between hidden and visible tooltips", ChatFormatting.RED);
            showHhitFeedback("HHIT: use /hhit on or /hhit off to pick an option", ChatFormatting.RED);
            info.cancel();
            return;
        }

        showHhitFeedback(statusMessage, enabled ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
        info.cancel();
    }

    private static void showHhitFeedback(@NonNull String message, @NonNull ChatFormatting color) {
        Minecraft.getInstance().gui.getChat().addClientSystemMessage(Component.literal(message).withStyle(color));
    }
}