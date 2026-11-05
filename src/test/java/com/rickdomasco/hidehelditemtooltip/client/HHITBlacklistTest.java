package com.rickdomasco.hidehelditemtooltip.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import org.junit.jupiter.api.Test;

class HHITBlacklistTest {
	private static final String ITEM_ID = "minecraft:diamond_sword";

	@Test
	void itemTypeExceptionMatchesCustomNamedVariantsOfThatItem() {
		HHITBlacklist blacklist = new HHITBlacklist();

		assertTrue(blacklist.add(ITEM_ID, null));
		assertTrue(blacklist.contains(ITEM_ID, null));
		assertTrue(blacklist.contains(ITEM_ID, "Named sword"));
		assertFalse(blacklist.contains("minecraft:iron_sword", "Named sword"));
	}

	@Test
	void customNameExceptionOnlyMatchesTheExactNamedItem() {
		HHITBlacklist blacklist = new HHITBlacklist();

		assertTrue(blacklist.add(ITEM_ID, "Named sword"));
		assertTrue(blacklist.contains(ITEM_ID, "Named sword"));
		assertFalse(blacklist.contains(ITEM_ID, "Another name"));
		assertFalse(blacklist.contains(ITEM_ID, null));
		assertFalse(blacklist.contains("minecraft:iron_sword", "Named sword"));
	}

	@Test
	void persistedExceptionsRoundTripWithoutReplacingOtherConfigValues() {
		HHITBlacklist blacklist = new HHITBlacklist();
		Properties properties = new Properties();
		properties.setProperty("hideHeldItemTooltip", "true");

		assertTrue(blacklist.add(ITEM_ID, null));
		assertTrue(blacklist.add(ITEM_ID, "Named sword"));
		blacklist.writeTo(properties);

		HHITBlacklist loaded = HHITBlacklist.readFrom(properties);

		assertTrue(loaded.contains(ITEM_ID, "Any name"));
		assertTrue(loaded.contains(ITEM_ID, "Named sword"));
		assertEquals(2, loaded.entries().size());
		assertEquals("true", properties.getProperty("hideHeldItemTooltip"));
	}
}
