package com.rickdomasco.hidehelditemtooltip.client.mixin;

import com.rickdomasco.hidehelditemtooltip.client.HHITModClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class SelectedItemNameMixin {

	@Inject(method = "extractSelectedItemName", at = @At("HEAD"), cancellable = true)
	private void hideHeldItemTooltip(GuiGraphicsExtractor extractor, CallbackInfo info) {
		if (HHITModClient.isHideHeldItemTooltipEnabled())
			info.cancel();
	}
}