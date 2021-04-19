package io.github.astrarre.networking.v0.api;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.internal.ModPacketHandlerImpl;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * communicate between server and client
 */
public interface ModPacketHandler {
	ModPacketHandler INSTANCE = ModPacketHandlerImpl.INSTANCE;

	@Environment (EnvType.CLIENT)
	void sendToServer(Id id, NBTagView output);
	void sendToClient(ServerPlayerEntity entity, Id id, NBTagView out);

	void registerAsyncClient(Id id, ClientReceiver receiver);
	void registerAsyncServer(Id id, ServerReceiver receiver);
	void registerSynchronizedClient(Id id, ClientReceiver receiver);
	void registerSynchronizedServer(Id id, ServerReceiver receiver);

	interface ClientReceiver {
		void accept(Id id, NBTagView tag);
	}

	interface ServerReceiver {
		void accept(NetworkMember member, Id id, NBTagView tag);
	}
}
