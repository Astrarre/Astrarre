package io.github.astrarre.util.internal.mixin;

import io.github.astrarre.util.internal.server.ServerMinecraftServer;
import io.github.astrarre.util.v0.fabric.MinecraftServers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin_ServerMCServer {
	@Inject(method = "run", at = @At(value = "INVOKE", target = "Ljava/lang/System;currentTimeMillis()J", ordinal = 0), remap = false)
	public void onStart(CallbackInfo ci) {
		MinecraftServers.activeServer = new ServerMinecraftServer((MinecraftServer) (Object) this);
	}

	@Inject(method = "stopServer", at = @At(value = "RETURN"))
	public void stop(CallbackInfo ci) {
		MinecraftServers.activeServer = null;
	}
}
