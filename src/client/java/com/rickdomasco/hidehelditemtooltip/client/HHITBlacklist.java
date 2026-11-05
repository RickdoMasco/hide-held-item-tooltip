package com.rickdomasco.hidehelditemtooltip.client;

import com.rickdomasco.hidehelditemtooltip.HHITMod;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public final class HHITBlacklist {
	private static final String PROPERTY_PREFIX = "blacklist.";
	private final Set<Entry> entries = new LinkedHashSet<>();

	public static HHITBlacklist readFrom(Properties properties) {
		HHITBlacklist blacklist = new HHITBlacklist();

		for (String key : properties.stringPropertyNames()) {
			if (!key.startsWith(PROPERTY_PREFIX) || !Boolean.parseBoolean(properties.getProperty(key)))
				continue;

			Entry entry = parseKey(key.substring(PROPERTY_PREFIX.length()));
			if (entry == null)
				HHITMod.LOGGER.warn("Ignoring malformed HHIT blacklist entry '{}'.", key);
			else
				blacklist.entries.add(entry);
		}

		return blacklist;
	}

	public boolean add(String itemId, String customName) {
		return entries.add(new Entry(itemId, customName));
	}

	public boolean remove(String itemId, String customName) {
		return entries.remove(new Entry(itemId, customName));
	}

	public boolean contains(String itemId, String customName) {
		return entries.contains(new Entry(itemId, null)) || entries.contains(new Entry(itemId, customName));
	}

	public List<Entry> entries() {
		return List.copyOf(entries);
	}

	public void writeTo(Properties properties) {
		properties.stringPropertyNames().stream()
				.filter(key -> key.startsWith(PROPERTY_PREFIX))
				.toList()
				.forEach(properties::remove);

		for (Entry entry : entries)
			properties.setProperty(PROPERTY_PREFIX + encode(entry), "true");
	}

	private static String encode(Entry entry) {
		String itemId = encodePart(entry.itemId());
		return entry.customName() == null
				? itemId + ".all"
				: itemId + ".name." + encodePart(entry.customName());
	}

	private static Entry parseKey(String key) {
		int separator = key.indexOf('.');
		if (separator <= 0)
			return null;

		try {
			String itemId = decodePart(key.substring(0, separator));
			if (itemId.isBlank())
				return null;

			String namePart = key.substring(separator + 1);
			if (namePart.equals("all"))
				return new Entry(itemId, null);
			if (namePart.startsWith("name."))
				return new Entry(itemId, decodePart(namePart.substring("name.".length())));
			return null;
		} catch (IllegalArgumentException exception) {
			return null;
		}
	}

	private static String encodePart(String value) {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
	}

	private static String decodePart(String value) {
		return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
	}

	public record Entry(String itemId, String customName) {
		public Entry {
			if (itemId == null || itemId.isBlank())
				throw new IllegalArgumentException("itemId must not be blank");
		}
	}
}
