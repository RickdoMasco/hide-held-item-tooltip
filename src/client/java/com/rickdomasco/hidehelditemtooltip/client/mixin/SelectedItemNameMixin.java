package com.rickdomasco.hidehelditemtooltip.client.mixin;

import com.rickdomasco.hidehelditemtooltip.client.HHITModClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class SelectedItemNameMixin {

	@Inject(method = "extractSelectedItemName", at = @At("HEAD"), cancellable = true)
	private void hideHeldItemTooltip(GuiGraphicsExtractor extractor, CallbackInfo info) {
		var player = Minecraft.getInstance().player;
		if (!HHITModClient.isHideHeldItemTooltipEnabled() || player == null)
			return;

		var stack = player.getMainHandItem();
		var customName = stack.get(DataComponents.CUSTOM_NAME);
		if (!stack.isEmpty()
				&& !HHITModClient.isBlacklisted(
						BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(),
						customName == null ? null : customName.getString()))
			info.cancel();
	}
}