package com.rickdomasco.hidehelditemtooltip.client.mixin;

import com.rickdomasco.hidehelditemtooltip.client.HHITModClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class SelectedItemNameMixin {

	@Inject(method = "extractSelectedItemName", at = @At("HEAD"), cancellable = true)
	private void hideHeldItemTooltip(CallbackInfo info) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null && HHITModClient.shouldHideHeldItemTooltip(minecraft.player.getMainHandItem()))
			info.cancel();
	}
}