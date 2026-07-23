package com.rickdomasco.hidehelditemtooltip.client;

import com.rickdomasco.hidehelditemtooltip.HHITMod;
import net.fabricmc.api.ClientModInitializer;

public class HHITModClient implements ClientModInitializer {
	private static boolean hideHeldItemTooltip = true;

	@Override
	public void onInitializeClient() {
		hideHeldItemTooltip = HHITConfig.loadHideHeldItemTooltip(hideHeldItemTooltip);
		HHITMod.LOGGER.info("HHIT Mod client config loaded: hideHeldItemTooltip={}", hideHeldItemTooltip);
	}

	public static boolean isHideHeldItemTooltipEnabled() {
		return hideHeldItemTooltip;
	}

	public static void setHideHeldItemTooltipEnabled(boolean enabled) {
		hideHeldItemTooltip = enabled;
		HHITConfig.saveHideHeldItemTooltip(enabled);
	}

	public static boolean toggleHideHeldItemTooltip() {
		hideHeldItemTooltip = !hideHeldItemTooltip;
		HHITConfig.saveHideHeldItemTooltip(hideHeldItemTooltip);
		return hideHeldItemTooltip;
	}
}