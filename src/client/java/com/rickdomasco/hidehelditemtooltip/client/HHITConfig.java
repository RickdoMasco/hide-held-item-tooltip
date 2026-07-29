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
		if (Files.notExists(CONFIG_FILE)) {
			saveHideHeldItemTooltip(defaultValue);
			return defaultValue;
		}

		Properties properties = new Properties();

		try (InputStream inputStream = Files.newInputStream(CONFIG_FILE)) {
			properties.load(inputStream);
		} catch (IOException exception) {
			HHITMod.LOGGER.warn(
					"Failed to read HHIT config at {}. Using default value {}.",
					CONFIG_FILE,
					defaultValue,
					exception);
			return defaultValue;
		}

		return Boolean.parseBoolean(
				properties.getProperty(HIDE_HELD_ITEM_TOOLTIP_KEY, Boolean.toString(defaultValue)));
	}

	public static void saveHideHeldItemTooltip(boolean enabled) {
		Properties properties = new Properties();
		properties.setProperty(HIDE_HELD_ITEM_TOOLTIP_KEY, Boolean.toString(enabled));

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