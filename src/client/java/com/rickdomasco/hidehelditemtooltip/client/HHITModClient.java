package com.rickdomasco.hidehelditemtooltip.client;

import com.rickdomasco.hidehelditemtooltip.HHITMod;
import net.fabricmc.api.ClientModInitializer;

public class HHITModClient implements ClientModInitializer {
	private static boolean hideHeldItemTooltip = true;
	private static HHITBlacklist blacklist = new HHITBlacklist();

	@Override
	public void onInitializeClient() {
		hideHeldItemTooltip = HHITConfig.loadHideHeldItemTooltip(hideHeldItemTooltip);
		blacklist = HHITConfig.loadBlacklist();
		HHITCommands.register();
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
		setHideHeldItemTooltipEnabled(!hideHeldItemTooltip);
		return hideHeldItemTooltip;
	}

	public static boolean isBlacklisted(String itemId, String customName) {
		return blacklist.contains(itemId, customName);
	}

	public static HHITBlacklist getBlacklist() {
		return blacklist;
	}

	public static void saveBlacklist() {
		HHITConfig.saveBlacklist(blacklist);
	}
}