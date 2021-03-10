package io.github.astrarre.gui.internal.mixin;

import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Mixin (MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow @Nullable public Screen currentScreen;

	@Inject (method = "openScreen", at = @At ("HEAD"))
	public void openScreen(Screen screen, CallbackInfo ci) {
		if (this.currentScreen != null) {
			RootContainerInternal internal = ((ScreenRootAccess) this.currentScreen).getRoot();
			if (internal != null) {
				internal.onClose();
			}
		}
	}
}
