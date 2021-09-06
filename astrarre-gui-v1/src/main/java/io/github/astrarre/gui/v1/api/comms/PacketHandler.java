package io.github.astrarre.gui.v1.api.comms;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;

import io.github.astrarre.gui.internal.comms.AbstractComms;
import io.github.astrarre.hash.v0.api.SHA256Hasher;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.SafeCloseable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PacketHandler extends SafeCloseable {
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

	void sendInfo(PacketKey key, Consumer<NBTagView.Builder> packet);

	void listen(PacketKey key, Consumer<NBTagView> consumer);
}
