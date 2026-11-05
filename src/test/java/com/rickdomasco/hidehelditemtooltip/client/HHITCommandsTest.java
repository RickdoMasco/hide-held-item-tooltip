package com.rickdomasco.hidehelditemtooltip.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

class HHITCommandsTest {
	@Test
	void listAcceptsUpperAndMixedCaseWithoutPlayerHand() throws Exception {
		List<String> feedback = new ArrayList<>();
		FabricClientCommandSource source = (FabricClientCommandSource) Proxy.newProxyInstance(
				FabricClientCommandSource.class.getClassLoader(),
				new Class<?>[] {FabricClientCommandSource.class},
				(proxy, method, arguments) -> {
					if (method.getName().equals("sendFeedback"))
						feedback.add(((Component) arguments[0]).getString());
					return null;
				});
		CommandDispatcher<FabricClientCommandSource> dispatcher = new CommandDispatcher<>();
		HHITCommands.register(dispatcher);

		dispatcher.execute("HHIT BLACKLIST LIST", source);
		dispatcher.execute("HhIt BlAcKlIsT LiSt", source);

		assertEquals(List.of("HHIT: no saved exceptions", "HHIT: no saved exceptions"), feedback);
	}

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
