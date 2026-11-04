package com.rickdomasco.hidehelditemtooltip.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.junit.jupiter.api.Test;

class HHITCommandsTest {
	@Test
	void registersTheLocalHhitCommandAndBlacklistSubcommands() {
		CommandDispatcher<FabricClientCommandSource> dispatcher = new CommandDispatcher<>();
		HHITCommands.register(dispatcher);

		var hhit = dispatcher.getRoot().getChild("hhit");
		assertNotNull(hhit, "client dispatcher should contain /hhit");
		assertNotNull(hhit.getChild("on"), "/hhit on should remain available");
		assertNotNull(hhit.getChild("off"), "/hhit off should remain available");
		var blacklist = hhit.getChild("blacklist");
		assertNotNull(blacklist, "/hhit blacklist should be registered");
		assertNotNull(blacklist.getChild("add"), "/hhit blacklist add should be registered");
		assertNotNull(blacklist.getChild("remove"), "/hhit blacklist remove should be registered");
		assertNotNull(blacklist.getChild("list"), "/hhit blacklist list should be registered");
	}
}
