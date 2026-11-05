package com.rickdomasco.hidehelditemtooltip.client;

import com.rickdomasco.hidehelditemtooltip.HHITMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class HHITModClient implements ClientModInitializer {
	private static boolean hideHeldItemTooltip = true;
	private static HHITItemBlacklist itemBlacklist = new HHITItemBlacklist();

	@Override
	public void onInitializeClient() {
		hideHeldItemTooltip = HHITConfig.loadHideHeldItemTooltip(hideHeldItemTooltip);
		itemBlacklist = HHITConfig.loadItemBlacklist();
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> HHITCommands.register(dispatcher));
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

	public static boolean isCurrentItemBlacklisted() {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null)
			return false;

		ItemStack itemStack = minecraft.player.getMainHandItem();
		return !itemStack.isEmpty() && itemBlacklist.matches(getItemId(itemStack), getCustomName(itemStack));
	}

	public static BlacklistUpdateResult addItemToBlacklist(ItemStack itemStack) {
		String itemId = getItemId(itemStack);
		String customName = getCustomName(itemStack);
		if (!itemBlacklist.add(itemId, customName))
			return BlacklistUpdateResult.UNCHANGED;
		if (!HHITConfig.saveItemBlacklist(itemBlacklist)) {
			itemBlacklist.remove(itemId, customName);
			return BlacklistUpdateResult.SAVE_FAILED;
		}
		return BlacklistUpdateResult.CHANGED;
	}

	public static BlacklistUpdateResult removeItemFromBlacklist(ItemStack itemStack) {
		String itemId = getItemId(itemStack);
		String customName = getCustomName(itemStack);
		if (!itemBlacklist.remove(itemId, customName))
			return BlacklistUpdateResult.UNCHANGED;
		if (!HHITConfig.saveItemBlacklist(itemBlacklist)) {
			itemBlacklist.add(itemId, customName);
			return BlacklistUpdateResult.SAVE_FAILED;
		}
		return BlacklistUpdateResult.CHANGED;
	}

	public static java.util.List<HHITItemBlacklist.Entry> getBlacklistedItems() {
		return itemBlacklist.entries();
	}

	public static String getItemId(ItemStack itemStack) {
		return BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString();
	}

	public static String getCustomName(ItemStack itemStack) {
		Component customName = itemStack.get(DataComponents.CUSTOM_NAME);
		return customName == null ? null : customName.getString();
	}

	public enum BlacklistUpdateResult {
		CHANGED,
		UNCHANGED,
		SAVE_FAILED
	}
}