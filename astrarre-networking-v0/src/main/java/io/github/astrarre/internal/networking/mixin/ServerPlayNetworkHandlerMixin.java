package io.github.astrarre.internal.networking.mixin;

import io.github.astrarre.v0.api.network.registry.ModPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin (ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow public ServerPlayerEntity player;

	@Inject (method = "onCustomPayload", at = @At ("HEAD"))
	private void onCustomPayloadAsync(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		if (this.player.getServerWorld().getServer().isOnThread()) {
			ModPacketHandler.INSTANCE.onReceive(packet);
		} else {
			ModPacketHandler.INSTANCE.onReceiveAsync(packet);
			NetworkThreadUtils.forceMainThread(packet, (ServerPlayPacketListener)this, this.player.getServerWorld());
		}
	}
}
