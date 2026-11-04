package com.rickdomasco.hidehelditemtooltip.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HHITSettingsScreen extends Screen {
	private final Screen previous;
	private Button toggleButton;

	public HHITSettingsScreen(Screen previous) {
		super(Component.translatable("hhitmod.settings.title"));
		this.previous = previous;
	}

	@Override
	protected void init() {
		super.init();

		int buttonWidth = 180;
		int x = (this.width - buttonWidth) / 2;
		int y = this.height / 2 - 25;

		toggleButton = Button.builder(
				Component.translatable(HHITModClient.isHideHeldItemTooltipEnabled() ? "hhitmod.settings.toggle.on" : "hhitmod.settings.toggle.off"),
				button -> {
					boolean enabled = !HHITModClient.isHideHeldItemTooltipEnabled();
					HHITModClient.setHideHeldItemTooltipEnabled(enabled);
					updateToggleButtonText();
				})
				.bounds(x, y, buttonWidth, 20)
				.build();
		addRenderableWidget(toggleButton);

		addRenderableWidget(Button.builder(Component.translatable("hhitmod.settings.done"), button -> onClose())
				.bounds(x, y + 30, buttonWidth, 20)
				.build());

		updateToggleButtonText();
	}

	private void updateToggleButtonText() {
		if (toggleButton != null) {
			toggleButton.setMessage(Component.translatable(HHITModClient.isHideHeldItemTooltipEnabled() ? "hhitmod.settings.toggle.on" : "hhitmod.settings.toggle.off"));
		}
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().setScreen(previous);
	}
}