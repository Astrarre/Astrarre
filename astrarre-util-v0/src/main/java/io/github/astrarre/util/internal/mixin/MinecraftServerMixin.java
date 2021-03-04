package io.github.astrarre.util.internal.mixin;

import io.github.astrarre.util.v0.fabric.MinecraftServers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.integrated.IntegratedServer;

@Mixin({IntegratedServer.class,
        MinecraftDedicatedServer.class
})
public abstract class MinecraftServerMixin {
	@Inject(method = "setupServer", at = @At("HEAD"))
	private void onSetup(CallbackInfoReturnable<Boolean> cir) {
		MinecraftServers.activeServer = (MinecraftServer) (Object) this;
	}

	@Inject(method = "shutdown", at = @At("TAIL"))
	private void onSetup(CallbackInfo ci) {
		MinecraftServers.activeServer = null;
	}
}
