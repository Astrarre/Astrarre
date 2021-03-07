package io.github.astrarre.networking.v0.api;

import java.util.function.Consumer;

import io.github.astrarre.networking.internal.ModPacketHandlerImpl;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface ModPacketHandler {
	ModPacketHandler INSTANCE = ModPacketHandlerImpl.INSTANCE;

	@Environment (EnvType.CLIENT)
	void sendToServer(Id id, Consumer<Output> output);

	void sendToClient(ServerPlayerEntity entity, Id id, Consumer<Output> out);

	void registerClient(Id id, ClientReceiver receiver);

	void registerServer(Id id, ServerReceiver receiver);

	void registerSynchronizedClient(Id id, ClientReceiver receiver);

	void registerSynchronizedServer(Id id, ServerReceiver receiver);

	interface ClientReceiver {
		void accept(Id id, Input buf);
	}

	interface ServerReceiver {
		void accept(NetworkMember member, Id id, Input buf);
	}
}
