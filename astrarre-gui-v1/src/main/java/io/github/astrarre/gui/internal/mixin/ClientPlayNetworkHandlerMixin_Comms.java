package io.github.astrarre.gui.internal.mixin;

import io.github.astrarre.gui.internal.comms.AbstractComms;
import io.github.astrarre.hash.v0.api.HashKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin_Comms {
	@Inject(method = "onCustomPayload", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;getChannel()Lnet/minecraft/util/Identifier;"), cancellable = true)
	public void onCustom(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if(AbstractComms.PACKET_ID.equals(packet.getChannel())) {
			PacketByteBuf buf = packet.getData();
			HashKey key = new HashKey(buf);
			var client = AbstractComms.getOrOpenPlayerComms(null, key, true);
			while(buf.readableBytes() > 0) {
				client.onReceive(buf);
			}
			ci.cancel();
		}
	}
}
