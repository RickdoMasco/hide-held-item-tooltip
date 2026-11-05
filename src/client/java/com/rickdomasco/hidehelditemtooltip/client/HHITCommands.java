package com.rickdomasco.hidehelditemtooltip.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Comparator;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public final class HHITCommands {
	private HHITCommands() {
	}

	public static void register() {
		ClientCommandRegistrationCallback.EVENT.register(
				(dispatcher, registryAccess) -> dispatcher.register(command()));
	}

	private static LiteralArgumentBuilder<FabricClientCommandSource> command() {
		return ClientCommands.literal("hhit")
				.executes(context -> toggle())
				.then(ClientCommands.literal("on").executes(context -> setEnabled(true)))
				.then(ClientCommands.literal("off").executes(context -> setEnabled(false)))
				.then(ClientCommands.literal("blacklist")
						.then(ClientCommands.literal("add").executes(context -> addHeldItem()))
						.then(ClientCommands.literal("remove").executes(context -> removeHeldItem()))
						.then(ClientCommands.literal("list").executes(context -> listBlacklist())));
	}

	private static int toggle() {
		boolean enabled = HHITModClient.toggleHideHeldItemTooltip();
		feedback(enabled ? "HHIT: tooltip hidden" : "HHIT: tooltip visible",
				enabled ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
		return 1;
	}

	private static int setEnabled(boolean enabled) {
		HHITModClient.setHideHeldItemTooltipEnabled(enabled);
		feedback(enabled ? "HHIT: tooltip hidden" : "HHIT: tooltip visible",
				enabled ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
		return 1;
	}

	private static int addHeldItem() {
		ItemStack stack = heldItem();
		if (stack == null)
			return requireHeldItem();

		String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		String customName = customName(stack);
		boolean added = HHITModClient.getBlacklist().add(itemId, customName);
		if (added)
			HHITModClient.saveBlacklist();

		feedback((added ? "HHIT: added exception for " : "HHIT: exception already exists for ")
				+ scope(itemId, customName), added ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
		return 1;
	}

	private static int removeHeldItem() {
		ItemStack stack = heldItem();
		if (stack == null)
			return requireHeldItem();

		String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		String customName = customName(stack);
		boolean removed = HHITModClient.getBlacklist().remove(itemId, customName);
		if (removed)
			HHITModClient.saveBlacklist();

		feedback((removed ? "HHIT: removed exception for " : "HHIT: no exception exists for ")
				+ scope(itemId, customName), removed ? ChatFormatting.YELLOW : ChatFormatting.RED);
		return 1;
	}

	private static int listBlacklist() {
		var entries = HHITModClient.getBlacklist().entries().stream()
				.sorted(Comparator.comparing(HHITBlacklist.Entry::itemId)
						.thenComparing(entry -> entry.customName() == null ? 0 : 1)
						.thenComparing(entry -> entry.customName() == null ? "" : entry.customName()))
				.toList();

		if (entries.isEmpty()) {
			feedback("HHIT: blacklist is empty", ChatFormatting.YELLOW);
			return 1;
		}

		feedback("HHIT: blacklist exceptions:", ChatFormatting.YELLOW);
		for (HHITBlacklist.Entry entry : entries)
			feedback("HHIT: " + scope(entry.itemId(), entry.customName()), ChatFormatting.WHITE);
		return entries.size();
	}

	private static ItemStack heldItem() {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null || minecraft.player.getMainHandItem().isEmpty())
			return null;
		return minecraft.player.getMainHandItem();
	}

	private static int requireHeldItem() {
		feedback("HHIT: hold an item in your main hand first", ChatFormatting.RED);
		return 0;
	}

	private static String customName(ItemStack stack) {
		var customName = stack.get(DataComponents.CUSTOM_NAME);
		return customName == null ? null : customName.getString();
	}

	private static String scope(String itemId, String customName) {
		return customName == null
				? "all items of type " + itemId
				: "only this exact custom-named item \"" + customName + "\" (" + itemId + ")";
	}

	private static void feedback(String message, ChatFormatting color) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null)
			minecraft.player.sendSystemMessage(net.minecraft.network.chat.Component.literal(message).withStyle(color));
	}
}
