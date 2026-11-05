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
	private static final String CONFIG_COMMENT = "HHIT Mod client configuration";

	private HHITConfig() {
	}

	public static boolean loadHideHeldItemTooltip(boolean defaultValue) {
		if (Files.notExists(CONFIG_FILE)) {
			saveHideHeldItemTooltip(defaultValue);
			return defaultValue;
		}

		Properties properties = loadProperties();
		if (properties == null)
			return defaultValue;

		return Boolean.parseBoolean(
				properties.getProperty(HIDE_HELD_ITEM_TOOLTIP_KEY, Boolean.toString(defaultValue)));
	}

	public static void saveHideHeldItemTooltip(boolean enabled) {
		Properties properties = loadPropertiesForUpdate();
		if (properties == null)
			return;
		properties.setProperty(HIDE_HELD_ITEM_TOOLTIP_KEY, Boolean.toString(enabled));
		saveProperties(properties);
	}

	public static HHITItemBlacklist loadItemBlacklist() {
		if (Files.notExists(CONFIG_FILE))
			return new HHITItemBlacklist();
		Properties properties = loadProperties();
		return properties == null ? new HHITItemBlacklist() : HHITItemBlacklist.readFrom(properties);
	}

	public static boolean saveItemBlacklist(HHITItemBlacklist blacklist) {
		Properties properties = loadPropertiesForUpdate();
		if (properties == null)
			return false;
		blacklist.writeTo(properties);
		return saveProperties(properties);
	}

	private static Properties loadPropertiesForUpdate() {
		return Files.notExists(CONFIG_FILE) ? new Properties() : loadProperties();
	}

	private static Properties loadProperties() {
		Properties properties = new Properties();
		try (InputStream inputStream = Files.newInputStream(CONFIG_FILE)) {
			properties.load(inputStream);
			return properties;
		} catch (IOException exception) {
			HHITMod.LOGGER.warn("Failed to read HHIT config at {}.", CONFIG_FILE, exception);
			return null;
		}
	}

	private static boolean saveProperties(Properties properties) {
		try {
			Files.createDirectories(CONFIG_FILE.getParent());
			try (OutputStream outputStream = Files.newOutputStream(CONFIG_FILE)) {
				properties.store(outputStream, CONFIG_COMMENT);
			}
			return true;
		} catch (IOException exception) {
			HHITMod.LOGGER.warn("Failed to write HHIT config at {}.", CONFIG_FILE, exception);
			return false;
		}
	}
}