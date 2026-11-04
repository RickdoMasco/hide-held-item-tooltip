package com.rickdomasco.hidehelditemtooltip.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.rickdomasco.hidehelditemtooltip.HHITMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class HHITModClient implements ClientModInitializer {
	private static final KeyMapping.Category HHIT_CATEGORY = KeyMapping.Category.register(HHITMod.id("hotkeys"));
	private static boolean hideHeldItemTooltip = true;
	private static final KeyMapping TOGGLE_KEY = new KeyMapping(
			"key.hhitmod.toggle",
			InputConstants.KEY_H,
			HHIT_CATEGORY);

	@Override
	public void onInitializeClient() {
		hideHeldItemTooltip = HHITConfig.loadHideHeldItemTooltip(hideHeldItemTooltip);
		KeyMappingHelper.registerKeyMapping(TOGGLE_KEY);
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!TOGGLE_KEY.consumeClick()) {
				return;
			}

			boolean enabled = toggleHideHeldItemTooltip();
			showToggleFeedback(enabled);
		});

		HHITMod.LOGGER.info("HHIT Mod client config loaded: hideHeldItemTooltip={}", hideHeldItemTooltip);
	}

	public static boolean isHideHeldItemTooltipEnabled() {
		return hideHeldItemTooltip;
	}

	public static boolean toggleHideHeldItemTooltip() {
		setHideHeldItemTooltipEnabled(!hideHeldItemTooltip);
		return hideHeldItemTooltip;
	}

	public static void setHideHeldItemTooltipEnabled(boolean enabled) {
		hideHeldItemTooltip = enabled;
		HHITConfig.saveHideHeldItemTooltip(enabled);
	}

	public static void showToggleFeedback(boolean enabled) {
		if (Minecraft.getInstance().player == null) {
			return;
		}

		Minecraft.getInstance().player.sendSystemMessage(
				Component.literal(enabled ? "HHIT: tooltip hidden" : "HHIT: tooltip visible")
						.withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
	}
}