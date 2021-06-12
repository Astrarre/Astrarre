package io.github.astrarre.networking.internal.mixin;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.ModPacketHandler;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.util.v0.api.Id;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements NetworkMember {
	@Override
	public void send(Id id, NBTagView output) {
		ModPacketHandler.INSTANCE.sendToClient(this.to(), id, output);
	}
}
