package io.github.astrarre.common.internal.mixin;

import io.github.astrarre.common.v0.api.Astrarre;
import io.github.astrarre.v0.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.integrated.IntegratedServer;

@Mixin({IntegratedServer.class,
        MinecraftDedicatedServer.class
})
public abstract class MinecraftServerMixin implements MinecraftServer {
	@Inject(method = "setupServer", at = @At("HEAD"))
	private void onSetup(CallbackInfoReturnable<Boolean> cir) {
		Astrarre.setCurrentServer(this);
	}

	@Inject(method = "shutdown", at = @At("TAIL"))
	private void onSetup(CallbackInfo ci) {
		Astrarre.setCurrentServer(null);
	}
}
