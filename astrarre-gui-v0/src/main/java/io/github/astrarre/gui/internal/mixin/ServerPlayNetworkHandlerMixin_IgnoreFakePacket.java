package io.github.astrarre.gui.internal.mixin;

import io.github.astrarre.gui.internal.AstrarreInitializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin_IgnoreFakePacket {
	@Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
	public void ignore(Packet<?> packet, CallbackInfo ci) {
		if(packet == AstrarreInitializer.FAKE) {
			ci.cancel();
		}
	}
}
