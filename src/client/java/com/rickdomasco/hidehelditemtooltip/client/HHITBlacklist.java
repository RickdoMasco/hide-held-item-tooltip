package com.rickdomasco.hidehelditemtooltip.client;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

public final class HHITBlacklist {
	private static final String PROPERTY_PREFIX = "blacklist.entry.";
	private static final String ITEM_SUFFIX = ".itemId";
	private static final String NAME_SUFFIX = ".customName";

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

	public void saveTo(Properties properties) {
		for (String key : new ArrayList<>(properties.stringPropertyNames())) {
			if (key.startsWith(PROPERTY_PREFIX))
				properties.remove(key);
		}

		int index = 0;
		for (Entry entry : entries) {
			String prefix = PROPERTY_PREFIX + index++;
			properties.setProperty(prefix + ITEM_SUFFIX, entry.itemId());
			if (entry.customName() != null)
				properties.setProperty(prefix + NAME_SUFFIX, entry.customName());
		}
	}

	public static HHITBlacklist loadFrom(Properties properties) {
		Map<Integer, Entry> indexedEntries = new TreeMap<>();

		for (String key : properties.stringPropertyNames()) {
			if (!key.startsWith(PROPERTY_PREFIX) || !key.endsWith(ITEM_SUFFIX))
				continue;

			String indexValue = key.substring(PROPERTY_PREFIX.length(), key.length() - ITEM_SUFFIX.length());
			int index;
			try {
				index = Integer.parseInt(indexValue);
			} catch (NumberFormatException exception) {
				continue;
			}

			String itemId = properties.getProperty(key);
			if (itemId == null || itemId.isBlank())
				continue;

			String nameKey = PROPERTY_PREFIX + index + NAME_SUFFIX;
			indexedEntries.put(index, new Entry(itemId, properties.getProperty(nameKey)));
		}

		HHITBlacklist blacklist = new HHITBlacklist();
		for (Entry entry : indexedEntries.values())
			blacklist.add(entry.itemId(), entry.customName());
		return blacklist;
	}

	public record Entry(String itemId, String customName) {
		public Entry {
			Objects.requireNonNull(itemId);
		}
	}
}
