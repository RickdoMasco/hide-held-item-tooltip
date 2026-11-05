package com.rickdomasco.hidehelditemtooltip.client;

import com.mojang.brigadier.CommandDispatcher;
import com.rickdomasco.hidehelditemtooltip.client.HHITModClient.BlacklistUpdateResult;
import java.util.List;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class HHITCommands {
	private HHITCommands() {
	}

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(ClientCommands.literal("hhit")
				.executes(context -> toggle(context.getSource()))
				.then(ClientCommands.literal("on").executes(context -> setEnabled(context.getSource(), true)))
				.then(ClientCommands.literal("off").executes(context -> setEnabled(context.getSource(), false)))
				.then(ClientCommands.literal("blacklist")
						.then(ClientCommands.literal("add").executes(context -> add(context.getSource())))
						.then(ClientCommands.literal("remove").executes(context -> remove(context.getSource())))
						.then(ClientCommands.literal("list").executes(context -> list(context.getSource())))));
	}

	private static int toggle(FabricClientCommandSource source) {
		boolean enabled = HHITModClient.toggleHideHeldItemTooltip();
		sendFeedback(source, enabled ? "HHIT: tooltip hidden" : "HHIT: tooltip visible",
				enabled ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
		return 1;
	}

	private static int setEnabled(FabricClientCommandSource source, boolean enabled) {
		HHITModClient.setHideHeldItemTooltipEnabled(enabled);
		sendFeedback(source, enabled ? "HHIT: tooltip hidden" : "HHIT: tooltip visible",
				enabled ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
		return 1;
	}

	private static int add(FabricClientCommandSource source) {
		ItemStack itemStack = source.getPlayer().getMainHandItem();
		if (itemStack.isEmpty()) {
			source.sendError(Component.literal("HHIT: hold an item in your main hand to use this command"));
			return 0;
		}

		String description = describe(itemStack);
		BlacklistUpdateResult result = HHITModClient.addItemToBlacklist(itemStack);
		switch (result) {
			case CHANGED -> sendFeedback(source, "HHIT: added exception for " + description, ChatFormatting.GREEN);
			case UNCHANGED ->
				sendFeedback(source, "HHIT: exception already exists for " + description, ChatFormatting.YELLOW);
			case SAVE_FAILED -> source.sendError(Component.literal("HHIT: failed to save exception for " + description));
		}
		return result == BlacklistUpdateResult.SAVE_FAILED ? 0 : 1;
	}

	private static int remove(FabricClientCommandSource source) {
		ItemStack itemStack = source.getPlayer().getMainHandItem();
		if (itemStack.isEmpty()) {
			source.sendError(Component.literal("HHIT: hold an item in your main hand to use this command"));
			return 0;
		}

		String description = describe(itemStack);
		BlacklistUpdateResult result = HHITModClient.removeItemFromBlacklist(itemStack);
		switch (result) {
			case CHANGED -> sendFeedback(source, "HHIT: removed exception for " + description, ChatFormatting.GREEN);
			case UNCHANGED ->
				sendFeedback(source, "HHIT: no exception exists for " + description, ChatFormatting.YELLOW);
			case SAVE_FAILED -> source.sendError(Component.literal("HHIT: failed to save exception for " + description));
		}
		return result == BlacklistUpdateResult.SAVE_FAILED ? 0 : 1;
	}

	private static int list(FabricClientCommandSource source) {
		List<HHITItemBlacklist.Entry> entries = HHITModClient.getBlacklistedItems();
		if (entries.isEmpty()) {
			sendFeedback(source, "HHIT: no saved exceptions", ChatFormatting.YELLOW);
			return 1;
		}

		for (HHITItemBlacklist.Entry entry : entries)
			sendFeedback(source, "HHIT: exception for " + entry.description(), ChatFormatting.YELLOW);
		return entries.size();
	}

	private static String describe(ItemStack itemStack) {
		return new HHITItemBlacklist.Entry(
				HHITModClient.getItemId(itemStack),
				HHITModClient.getCustomName(itemStack)).description();
	}

	private static void sendFeedback(FabricClientCommandSource source, String message, ChatFormatting color) {
		source.sendFeedback(Component.literal(message).withStyle(color));
	}
}
