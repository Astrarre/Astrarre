package io.github.astrarre.networking.mixin;

import java.util.function.Consumer;

import io.github.astrarre.networking.v0.api.ModPacketHandler;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.util.v0.api.Id;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements NetworkMember {
	@Override
	public void send(Id id, Consumer<Output> output) {
		ModPacketHandler.INSTANCE.sendToClient(this.to(), id, output);
	}
}
