package com.rickdomasco.hidehelditemtooltip.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HHITSettingsScreen extends Screen {
	private final Screen previous;
	private Button toggleButton;

	public HHITSettingsScreen(Screen previous) {
		super(Component.literal("HHIT Settings"));
		this.previous = previous;
	}

	@Override
	protected void init() {
		super.init();

		int buttonWidth = 180;
		int x = (this.width - buttonWidth) / 2;
		int y = this.height / 2 - 25;

		toggleButton = Button.builder(
				Component.literal(buildToggleText()),
				button -> {
					boolean enabled = !HHITModClient.isHideHeldItemTooltipEnabled();
					HHITModClient.setHideHeldItemTooltipEnabled(enabled);
					updateToggleButtonText();
				})
				.bounds(x, y, buttonWidth, 20)
				.build();
		addRenderableWidget(toggleButton);

		addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
				.bounds(x, y + 30, buttonWidth, 20)
				.build());

		updateToggleButtonText();
	}

	private String buildToggleText() {
		return HHITModClient.isHideHeldItemTooltipEnabled() ? "Hide tooltip: ON" : "Hide tooltip: OFF";
	}

	private void updateToggleButtonText() {
		if (toggleButton != null) {
			toggleButton.setMessage(Component.literal(buildToggleText()));
		}
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().gui.setScreen(previous);
	}
}
