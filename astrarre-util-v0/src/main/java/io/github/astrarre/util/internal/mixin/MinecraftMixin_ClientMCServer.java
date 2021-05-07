package io.github.astrarre.util.internal.mixin;

import io.github.astrarre.util.internal.client.ClientMinecraftServer;
import io.github.astrarre.util.internal.server.ServerMinecraftServer;
import io.github.astrarre.util.v0.fabric.MinecraftServers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

@Mixin(Minecraft.class)
public class MinecraftMixin_ClientMCServer {
	@Inject(method = "run", at = @At(value = "INVOKE", target = "Ljava/lang/System;currentTimeMillis()J", ordinal = 0, remap = false), remap = false)
	public void onStart(CallbackInfo ci) {
		MinecraftServers.activeServer = new ClientMinecraftServer((Minecraft) (Object) this);
	}

	@Inject(method = "stop", at = @At(value = "RETURN"))
	public void stop(CallbackInfo ci) {
		MinecraftServers.activeServer = null;
	}
}
