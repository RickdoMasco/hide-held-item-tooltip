package com.rickdomasco.hidehelditemtooltip.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import org.junit.jupiter.api.Test;

class HHITBlacklistTest {
	@Test
	void matchesItemTypeAndExactCustomName() {
		HHITBlacklist blacklist = new HHITBlacklist();
		blacklist.add("minecraft:diamond_pickaxe", null);
		blacklist.add("minecraft:stick", "Builder's wand");

		assertTrue(blacklist.matches("minecraft:diamond_pickaxe", null));
		assertTrue(blacklist.matches("minecraft:diamond_pickaxe", "Fortune III"));
		assertTrue(blacklist.matches("minecraft:stick", "Builder's wand"));
		assertFalse(blacklist.matches("minecraft:stick", null));
		assertFalse(blacklist.matches("minecraft:stick", "Fighter's wand"));
		assertFalse(blacklist.matches("minecraft:diamond_axe", null));
	}

	@Test
	void removesOnlyTheMatchingExceptionKey() {
		HHITBlacklist blacklist = new HHITBlacklist();
		blacklist.add("minecraft:stick", "Builder's wand");

		assertTrue(blacklist.remove("minecraft:stick", "Builder's wand"));
		assertFalse(blacklist.matches("minecraft:stick", "Builder's wand"));
		assertFalse(blacklist.matches("minecraft:stick", null));

		blacklist.add("minecraft:stick", null);
		blacklist.add("minecraft:stick", "Builder's wand");
		assertTrue(blacklist.remove("minecraft:stick", null));
		assertTrue(blacklist.matches("minecraft:stick", "Builder's wand"));
		assertFalse(blacklist.matches("minecraft:stick", "Fighter's wand"));
		assertTrue(blacklist.remove("minecraft:stick", "Builder's wand"));
		assertFalse(blacklist.matches("minecraft:stick", null));
	}

	@Test
	void serializesExceptionsWithoutReplacingOtherConfigProperties() {
		Properties properties = new Properties();
		properties.setProperty("hideHeldItemTooltip", "false");

		HHITBlacklist blacklist = new HHITBlacklist();
		blacklist.add("minecraft:diamond_pickaxe", null);
		blacklist.add("minecraft:stick", "Builder's wand");
		blacklist.saveTo(properties);

		HHITBlacklist loaded = HHITBlacklist.loadFrom(properties);

		assertFalse(Boolean.parseBoolean(properties.getProperty("hideHeldItemTooltip")));
		assertEquals(2, loaded.entries().size());
		assertTrue(loaded.matches("minecraft:diamond_pickaxe", "Fortune III"));
		assertTrue(loaded.matches("minecraft:stick", "Builder's wand"));
		assertFalse(loaded.matches("minecraft:stick", "Fighter's wand"));

		loaded.remove("minecraft:stick", "Builder's wand");
		loaded.saveTo(properties);
		HHITBlacklist updated = HHITBlacklist.loadFrom(properties);
		assertEquals(1, updated.entries().size());
		assertFalse(updated.matches("minecraft:stick", "Builder's wand"));
	}
}
