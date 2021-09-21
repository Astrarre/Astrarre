package io.github.astrarre.components.v0.api.components.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.github.astrarre.components.v0.api.components.Component;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

public interface ClientComponent<C, V, T extends Component<C, V>> {
	T component();

	List<UnsafeClientRef<V>> entriesForActiveWorld();

	interface Chunk<V, T extends Component<WorldChunk, V>> extends ClientComponent<WorldChunk, V, T> {
		Map<ChunkPos, UnsafeClientRef<WorldChunk>> chunksForActiveWorld();
	}

	interface Player<V, T extends Component<AbstractClientPlayerEntity, V>> extends ClientComponent<AbstractClientPlayerEntity, V, T> {
		Map<UUID, UnsafeClientRef<AbstractClientPlayerEntity>> playersForActiveWorld();
	}


}
