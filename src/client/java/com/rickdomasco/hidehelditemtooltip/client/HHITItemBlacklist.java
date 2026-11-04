package com.rickdomasco.hidehelditemtooltip.client;

import com.rickdomasco.hidehelditemtooltip.HHITMod;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public final class HHITItemBlacklist {
	private static final String PROPERTY_PREFIX = "blacklist.";
	private static final String ALL_NAMES_MARKER = "*";

	private final Set<Entry> entries = new LinkedHashSet<>();

	public boolean add(String itemId, String customName) {
		return entries.add(new Entry(itemId, customName));
	}

	public boolean remove(String itemId, String customName) {
		return entries.remove(new Entry(itemId, customName));
	}

	public boolean matches(String itemId, String customName) {
		return entries.contains(new Entry(itemId, null))
				|| (customName != null && entries.contains(new Entry(itemId, customName)));
	}

	public List<Entry> entries() {
		return List.copyOf(entries);
	}

	public List<Entry> entriesFor(String itemId, String customName) {
		return entries.stream()
				.filter(entry -> entry.itemId().equals(itemId)
						&& (entry.customName() == null || entry.customName().equals(customName)))
				.toList();
	}

	public void writeTo(Properties properties) {
		properties.stringPropertyNames().stream()
				.filter(key -> key.startsWith(PROPERTY_PREFIX))
				.forEach(properties::remove);

		for (Entry entry : entries) {
			String encodedItemId = encode(entry.itemId());
			String encodedName = entry.customName() == null ? ALL_NAMES_MARKER : encode(entry.customName());
			properties.setProperty(PROPERTY_PREFIX + encodedItemId + "." + encodedName, "true");
		}
	}

	public static HHITItemBlacklist readFrom(Properties properties) {
		HHITItemBlacklist blacklist = new HHITItemBlacklist();
		for (String key : properties.stringPropertyNames()) {
			if (!key.startsWith(PROPERTY_PREFIX))
				continue;

			String[] parts = key.substring(PROPERTY_PREFIX.length()).split("\\.", 2);
			if (parts.length != 2)
				continue;

			try {
				String itemId = decode(parts[0]);
				String customName = parts[1].equals(ALL_NAMES_MARKER) ? null : decode(parts[1]);
				blacklist.add(itemId, customName);
			} catch (IllegalArgumentException exception) {
				HHITMod.LOGGER.warn("Ignoring malformed HHIT blacklist entry in config: {}", key, exception);
			}
		}
		return blacklist;
	}

	private static String encode(String value) {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
	}

	private static String decode(String value) {
		return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
	}

	public record Entry(String itemId, String customName) {
		public Entry {
			if (itemId == null || itemId.isBlank())
				throw new IllegalArgumentException("itemId must not be blank");
		}

		public String description() {
			return customName == null
					? "all items of type " + itemId
					: "only the exact custom-named item \"" + customName + "\" (" + itemId + ")";
		}
	}
}
