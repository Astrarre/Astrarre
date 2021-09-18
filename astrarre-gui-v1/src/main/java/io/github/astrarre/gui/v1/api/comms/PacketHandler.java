package io.github.astrarre.gui.v1.api.comms;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;

import io.github.astrarre.gui.internal.comms.AbstractComms;
import io.github.astrarre.gui.internal.comms.LocalComms;
import io.github.astrarre.hash.v0.api.SHA256Hasher;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.SafeCloseable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PacketHandler extends SafeCloseable {
	static PacketHandler local(Id handler, String instance, boolean isClient) {
		try(SHA256Hasher hasher = SHA256Hasher.getPooled()) {
			hasher.putIdentifier(handler);
			hasher.putString(instance, StandardCharsets.UTF_8);
			return LocalComms.getOrCreate(hasher.hashC(), isClient);
		}
	}

	static PacketHandler player(Id handler, PlayerEntity entity) {
		return player(handler, entity, !(entity instanceof ServerPlayerEntity));
	}

	static PacketHandler player(Id handler, PlayerEntity entity, boolean isClient) {
		// the id should be sent from the server, maybe we should send
		try(SHA256Hasher hasher = SHA256Hasher.getPooled()) {
			UUID uuid = entity.getUuid();
			hasher.putLong(uuid.getLeastSignificantBits());
			hasher.putLong(uuid.getMostSignificantBits());
			hasher.putIdentifier(handler);
			return AbstractComms.getOrOpenPlayerComms(entity, hasher.hashC(), isClient);
		}
	}

	/**
	 * instead of sending one packet for every send info call, this method tells the packet handler to record all calls going through
	 */
	void startQueue();

	/**
	 * this method sends a batched packet with all the packets recorded since {@link #startQueue()}
	 */
	void flushQueue();

	void sendInfo(PacketKey key, NBTagView view);

	default void sendInfo(PacketKey key, Consumer<NBTagView.Builder> packet) {
		NBTagView.Builder builder = NBTagView.builder();
		packet.accept(builder);
		this.sendInfo(key, builder);
	}

	void listen(PacketKey key, Consumer<NBTagView> consumer);
}
