package io.github.astrarre.gui.internal.mixin;

import io.github.astrarre.gui.internal.comms.AbstractComms;
import io.github.astrarre.hash.v0.api.HashKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin_Comms implements ServerPlayPacketListener {
	@Shadow public ServerPlayerEntity player;

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	public void enforce(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
		if(AbstractComms.PACKET_ID.equals(packet.getChannel())) {
			PacketByteBuf buf = packet.getData();
			HashKey key = new HashKey(buf);
			var client = AbstractComms.getOrOpenPlayerComms(this.player, key, false);
			while(buf.readableBytes() > 0) {
				client.onReceive(buf);
			}
			ci.cancel();
		}
	}
}
