package com.rickdomasco.hidehelditemtooltip.client.mixin;

import com.rickdomasco.hidehelditemtooltip.client.HHITBlacklist;
import com.rickdomasco.hidehelditemtooltip.client.HHITModClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
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

        if (normalized.isEmpty())
            return;

        String[] parts = normalized.split(" ", 2);

        if (!parts[0].equalsIgnoreCase("/hhit"))
            return;

        if (addToRecent)
            Minecraft.getInstance().gui.getChat().addRecentChat(normalized);

        String argument = parts.length > 1 ? parts[1].trim() : "";

        String[] argumentParts = argument.split("\\s+", 2);
        if (argumentParts[0].equalsIgnoreCase("blacklist")) {
            handleBlacklist(argumentParts.length > 1 ? argumentParts[1] : "");
            info.cancel();
            return;
        }

        if (!argument.isEmpty() && !argument.equalsIgnoreCase("on") && !argument.equalsIgnoreCase("off")) {
            showHhitFeedback("HHI(N)T: use \"/hhit\" to swap between hidden and visible tooltips", ChatFormatting.RED);
            showHhitFeedback("HHI(N)T: use \"/hhit on\" or \"/hhit off\" to pick an option", ChatFormatting.RED);
            info.cancel();
            return;
        }

        boolean enabled = argument.isEmpty() ? HHITModClient.toggleHideHeldItemTooltip()
                : argument.equalsIgnoreCase("on");

        if (!argument.isEmpty())
            HHITModClient.setHideHeldItemTooltipEnabled(enabled);

        showHhitFeedback(enabled ? "HHIT: tooltip hidden" : "HHIT: tooltip visible",
                enabled ? ChatFormatting.GREEN : ChatFormatting.YELLOW);

        info.cancel();
    }

    private static void handleBlacklist(String argument) {
        String trimmed = argument.trim();
        String[] arguments = trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");

        if (arguments.length == 1 && arguments[0].equalsIgnoreCase("list")) {
            var exceptions = HHITModClient.getBlacklistExceptions();
            if (exceptions.isEmpty()) {
                showHhitFeedback("HHIT: no saved exceptions", ChatFormatting.YELLOW);
                return;
            }

            showHhitFeedback("HHIT: saved exceptions:", ChatFormatting.YELLOW);
            for (HHITBlacklist.Entry entry : exceptions) {
                showHhitFeedback(" - " + describeTarget(entry.itemId(), entry.customName())
                        + " (popup visible even while hiding is enabled)", ChatFormatting.YELLOW);
            }
            return;
        }

        if (arguments.length != 1
                || (!arguments[0].equalsIgnoreCase("add") && !arguments[0].equalsIgnoreCase("remove"))) {
            showHhitFeedback("HHIT: use \"/hhit blacklist add\", \"/hhit blacklist remove\", or \"/hhit blacklist list\"",
                    ChatFormatting.RED);
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.player.getMainHandItem().isEmpty()) {
            showHhitFeedback("HHIT: hold an item in your main hand to add or remove an exception",
                    ChatFormatting.RED);
            return;
        }

        ItemStack stack = minecraft.player.getMainHandItem();
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        String customName = stack.has(DataComponents.CUSTOM_NAME)
                ? stack.get(DataComponents.CUSTOM_NAME).getString()
                : null;
        String target = describeTarget(itemId, customName);

        if (arguments[0].equalsIgnoreCase("add")) {
            boolean added = HHITModClient.addBlacklistException(itemId, customName);
            showHhitFeedback(added
                    ? "HHIT: added exception for " + target
                            + ". Its popup will stay visible even while hiding is enabled."
                    : "HHIT: exception already saved for " + target
                            + ". Its popup stays visible even while hiding is enabled.",
                    added ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
        } else {
            boolean removed = HHITModClient.removeBlacklistException(itemId, customName);
            showHhitFeedback(removed
                    ? "HHIT: removed exception for " + target
                            + ". Its popup will be hidden again while hiding is enabled."
                    : "HHIT: no exception was saved for " + target + ". No change was made.",
                    removed ? ChatFormatting.YELLOW : ChatFormatting.RED);
        }
    }

    private static String describeTarget(String itemId, String customName) {
        return customName == null
                ? "all items of type " + itemId
                : "only " + itemId + " with the exact custom name \"" + customName + "\"";
    }

    private static void showHhitFeedback(@NonNull String message, @NonNull ChatFormatting color) {
        Minecraft.getInstance().gui.getChat().addClientSystemMessage(Component.literal(message).withStyle(color));
    }
}