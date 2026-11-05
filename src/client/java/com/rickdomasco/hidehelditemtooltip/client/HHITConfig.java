package com.rickdomasco.hidehelditemtooltip.client;

import com.rickdomasco.hidehelditemtooltip.HHITMod;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

public final class HHITConfig {
	private static final Path CONFIG_FILE = FabricLoader.getInstance()
			.getConfigDir()
			.resolve(HHITMod.MOD_ID + ".properties");

	private static final String HIDE_HELD_ITEM_TOOLTIP_KEY = "hideHeldItemTooltip";

	private HHITConfig() {
	}

	public static boolean loadHideHeldItemTooltip(boolean defaultValue) {
		Properties properties = loadProperties();
		if (properties == null)
			return defaultValue;

		boolean enabled = Boolean.parseBoolean(
				properties.getProperty(HIDE_HELD_ITEM_TOOLTIP_KEY, Boolean.toString(defaultValue)));
		if (Files.notExists(CONFIG_FILE))
			saveHideHeldItemTooltip(enabled);
		return enabled;
	}

	public static void saveHideHeldItemTooltip(boolean enabled) {
		Properties properties = loadProperties();
		if (properties == null)
			return;
		properties.setProperty(HIDE_HELD_ITEM_TOOLTIP_KEY, Boolean.toString(enabled));
		saveProperties(properties);
	}

	public static HHITBlacklist loadBlacklist() {
		Properties properties = loadProperties();
		return properties == null ? new HHITBlacklist() : HHITBlacklist.readFrom(properties);
	}

	public static void saveBlacklist(HHITBlacklist blacklist) {
		Properties properties = loadProperties();
		if (properties == null)
			return;
		blacklist.writeTo(properties);
		saveProperties(properties);
	}

	private static Properties loadProperties() {
		Properties properties = new Properties();
		if (Files.notExists(CONFIG_FILE))
			return properties;

		try (InputStream inputStream = Files.newInputStream(CONFIG_FILE)) {
			properties.load(inputStream);
		} catch (IOException exception) {
			HHITMod.LOGGER.warn("Failed to read HHIT config at {}.", CONFIG_FILE, exception);
			return null;
		}

		return properties;
	}

	private static void saveProperties(Properties properties) {
		try {
			Files.createDirectories(CONFIG_FILE.getParent());
			try (OutputStream outputStream = Files.newOutputStream(CONFIG_FILE)) {
				properties.store(outputStream, "HHIT Mod client configuration");
			}
		} catch (IOException exception) {
			HHITMod.LOGGER.warn("Failed to write HHIT config at {}.", CONFIG_FILE, exception);
		}
	}
}