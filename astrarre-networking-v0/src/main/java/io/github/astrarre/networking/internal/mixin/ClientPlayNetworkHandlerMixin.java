package io.github.astrarre.networking.internal.mixin;

import io.github.astrarre.networking.internal.ModPacketHandlerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
@Mixin (ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Final @Shadow private MinecraftClient client;

	@Inject (method = "onCustomPayload", at = @At ("HEAD"), cancellable = true)
	private void onCustomPayloadAsync(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if (!this.client.isOnThread()) {
			if(ModPacketHandlerImpl.INSTANCE.onReceiveAsync(packet)) {
				ci.cancel();
			}
		}
	}

	@Inject (method = "onCustomPayload",
			at = @At (value = "INVOKE",
					target =
							"Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;" + "Lnet/minecraft/network" +
							"/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",
					shift = At.Shift.AFTER),
			cancellable = true)
	private void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if (ModPacketHandlerImpl.INSTANCE.onReceive(packet)) {
			ci.cancel();
		}
	}
}
