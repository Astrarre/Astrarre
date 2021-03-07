package io.github.astrarre.networking.v0.api.network;

import java.util.function.Consumer;

import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * A minor abstraction over ServerPlayerEntity.
 *
 * At the time of writing, NetworkMember is implemented on ServerPlayerEntity, so for fabric/astrarre mods it is safe to cast directly to it.
 *
 * todo in Astrarre make ServerPlayerEntity implement NetworkMember
 */
public interface NetworkMember {
	void send(Id id, Consumer<Output> output);

	default ServerPlayerEntity to() {
		return (ServerPlayerEntity) this;
	}
}
