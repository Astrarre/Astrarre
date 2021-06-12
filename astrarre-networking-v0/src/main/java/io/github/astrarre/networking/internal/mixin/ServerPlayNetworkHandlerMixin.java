package io.github.astrarre.networking.internal.mixin;

import io.github.astrarre.networking.internal.ModPacketHandlerImpl;
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
public abstract class ServerPlayNetworkHandlerMixin implements ServerPlayPacketListener {
	@Shadow public ServerPlayerEntity player;

	@Inject (method = "onCustomPayload", at = @At ("HEAD"))
	private void onCustomPayloadAsync(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		if (this.player.getServerWorld().getServer().isOnThread()) {
			ModPacketHandlerImpl.INSTANCE.onReceive(this.player, packet);
		} else {
			ModPacketHandlerImpl.INSTANCE.onReceiveAsync(this.player, packet);
			NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		}
	}
}
