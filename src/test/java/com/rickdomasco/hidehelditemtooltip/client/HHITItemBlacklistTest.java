package com.rickdomasco.hidehelditemtooltip.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import org.junit.jupiter.api.Test;

class HHITItemBlacklistTest {
	@Test
	void itemTypeEntryMatchesEveryNameOfThatItem() {
		HHITItemBlacklist blacklist = new HHITItemBlacklist();
		blacklist.add("minecraft:diamond_sword", null);

		assertTrue(blacklist.matches("minecraft:diamond_sword", null));
		assertTrue(blacklist.matches("minecraft:diamond_sword", "Excalibur"));
		assertFalse(blacklist.matches("minecraft:iron_sword", "Excalibur"));
	}

	@Test
	void customNameEntryMatchesOnlyThatExactName() {
		HHITItemBlacklist blacklist = new HHITItemBlacklist();
		blacklist.add("minecraft:diamond_sword", "Excalibur");

		assertTrue(blacklist.matches("minecraft:diamond_sword", "Excalibur"));
		assertFalse(blacklist.matches("minecraft:diamond_sword", "Other"));
		assertFalse(blacklist.matches("minecraft:diamond_sword", null));
	}

	@Test
	void removeDeletesOnlyTheMatchingEntry() {
		HHITItemBlacklist blacklist = new HHITItemBlacklist();
		blacklist.add("minecraft:diamond_sword", null);
		blacklist.add("minecraft:diamond_sword", "Excalibur");

		assertTrue(blacklist.remove("minecraft:diamond_sword", "Excalibur"));
		assertTrue(blacklist.matches("minecraft:diamond_sword", "Other"));
		assertEquals(1, blacklist.entries().size());
	}

	@Test
	void entriesForOnlyReturnsExceptionsThatApplyToTheSelectedItem() {
		HHITItemBlacklist blacklist = new HHITItemBlacklist();
		blacklist.add("minecraft:diamond_sword", null);
		blacklist.add("minecraft:diamond_sword", "Excalibur");
		blacklist.add("minecraft:iron_sword", "Excalibur");

		assertEquals(2, blacklist.entriesFor("minecraft:diamond_sword", "Excalibur").size());
		assertEquals(1, blacklist.entriesFor("minecraft:diamond_sword", "Other").size());
		assertEquals(1, blacklist.entriesFor("minecraft:diamond_sword", null).size());
	}

	@Test
	void propertiesRoundTripPreservesItemAndExactCustomName() {
		HHITItemBlacklist blacklist = new HHITItemBlacklist();
		blacklist.add("minecraft:diamond_sword", null);
		blacklist.add("minecraft:diamond_sword", "Éxact = Name");
		Properties properties = new Properties();
		properties.setProperty("hideHeldItemTooltip", "false");

		blacklist.writeTo(properties);
		HHITItemBlacklist loaded = HHITItemBlacklist.readFrom(properties);

		assertEquals("false", properties.getProperty("hideHeldItemTooltip"));
		assertTrue(loaded.matches("minecraft:diamond_sword", "Any name"));
		assertTrue(loaded.matches("minecraft:diamond_sword", "Éxact = Name"));
		assertFalse(loaded.matches("minecraft:iron_sword", "Éxact = Name"));
		assertEquals(2, loaded.entries().size());
	}

	@Test
	void entryDescriptionsDistinguishItemTypesFromExactNames() {
		assertEquals(
				"all items of type minecraft:diamond_sword",
				new HHITItemBlacklist.Entry("minecraft:diamond_sword", null).description());
		assertEquals(
				"only the exact custom-named item \"Excalibur\" (minecraft:diamond_sword)",
				new HHITItemBlacklist.Entry("minecraft:diamond_sword", "Excalibur").description());
	}
}
