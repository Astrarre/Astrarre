package io.github.astrarre.recipe.internal.mixin;

import io.github.astrarre.recipe.internal.ReloadableManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin_Invalidate {
	@Inject(method = "shutdown", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ServerResourceManager;close()V"))
	public void onShutdown(CallbackInfo ci) {
		ReloadableManager.SERVER.invalidate();
	}
}
