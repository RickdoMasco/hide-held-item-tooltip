package com.rickdomasco.hidehelditemtooltip.client;

import com.rickdomasco.hidehelditemtooltip.HHITMod;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public class HHITModClient implements ClientModInitializer {
	private static boolean hideHeldItemTooltip = true;
	private static HHITBlacklist blacklist = new HHITBlacklist();

	@Override
	public void onInitializeClient() {
		hideHeldItemTooltip = HHITConfig.loadHideHeldItemTooltip(hideHeldItemTooltip);
		blacklist = HHITConfig.loadBlacklist();
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

	public static boolean shouldHideHeldItemTooltip(ItemStack stack) {
		if (!hideHeldItemTooltip)
			return false;

		String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		String customName = stack.has(DataComponents.CUSTOM_NAME)
				? stack.get(DataComponents.CUSTOM_NAME).getString()
				: null;
		return !blacklist.matches(itemId, customName);
	}

	public static boolean addBlacklistException(String itemId, String customName) {
		boolean added = blacklist.add(itemId, customName);
		if (added)
			HHITConfig.saveBlacklist(blacklist);
		return added;
	}

	public static boolean removeBlacklistException(String itemId, String customName) {
		boolean removed = blacklist.remove(itemId, customName);
		if (removed)
			HHITConfig.saveBlacklist(blacklist);
		return removed;
	}

	public static List<HHITBlacklist.Entry> getBlacklistExceptions() {
		return blacklist.entries();
	}
}