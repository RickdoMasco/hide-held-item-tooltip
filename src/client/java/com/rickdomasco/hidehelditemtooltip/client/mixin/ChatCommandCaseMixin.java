package com.rickdomasco.hidehelditemtooltip.client.mixin;

import java.util.Locale;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatScreen.class)
public class ChatCommandCaseMixin {
	@ModifyVariable(method = "handleChatInput", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private String normalizeHhitCommandCase(String message) {
		String normalized = ((ChatScreen) (Object) this).normalizeChatMessage(message);
		int separator = normalized.indexOf(' ');
		String command = separator < 0 ? normalized : normalized.substring(0, separator);
		return command.equalsIgnoreCase("/hhit") ? normalized.toLowerCase(Locale.ROOT) : message;
	}
}
