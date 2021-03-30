package io.github.astrarre.networking.v0.api.network;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * A minor abstraction over ServerPlayerEntity.
 *
 * At the time of writing, NetworkMember is implemented on ServerPlayerEntity, so for fabric/astrarre mods it is safe to cast directly to it.
 */
public interface NetworkMember {
	void send(Id id, NBTagView output);

	default ServerPlayerEntity to() {
		return (ServerPlayerEntity) this;
	}
}
