package io.github.astrarre.components.v0.fabric.builder;

import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.internal.mixin.ReadOnlyChunkAccess;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.factory.ComponentManager;
import io.github.astrarre.components.v0.fabric.FabricByteSerializer;
import io.github.astrarre.itemview.v0.api.Serializer;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkComponentBuilder<V, T extends Component<WorldChunk, V>> extends SerializableComponentBuilder<WorldChunk, V, T> {
	protected ChunkComponentBuilder(ComponentManager<WorldChunk> manager,
			Map<String, Pair<Component<WorldChunk, ?>, FabricByteSerializer<?>>> synchronize,
			Map<String, Pair<Component<WorldChunk, ?>, Serializer<?>>> internal) {
		super(manager, synchronize, internal);
	}

	public static <V, T extends Component<WorldChunk, V>> ChunkComponentBuilder<V, T> chunk() {
		return new ChunkComponentBuilder<>(ComponentsInternal.CHUNK_MANAGER,
		                                   ComponentsInternal.SYNC_CHUNK_INTERNAL,
		                                   ComponentsInternal.SERIALIZE_CHUNK_INTERNAL);
	}

	@Nullable
	public static <V, C extends WorldChunk> CustomPayloadS2CPacket sync(Identifier packetId,
			FabricByteSerializer<V> serializer,
			Component<C, V> component,
			C context,
			boolean send) {
		return syncWorldBased(packetId,
		                      serializer,
		                      component,
		                      context,
		                      send,
		                      WorldChunk::getWorld,
		                      (c, pkt) -> ((ServerWorld) c.getWorld()).getChunkManager().threadedAnvilChunkStorage.getPlayersWatchingChunk(c.getPos(), false)
				                      .map(m -> m.networkHandler)
				                      .forEach(m -> m.sendPacket(pkt)),
		                      (c, buf) -> buf.writeChunkPos(c.getPos()));
	}

	@Nullable
	public static WorldChunk from(Chunk chunk) {
		if(chunk instanceof ReadOnlyChunkAccess a) {
			return a.getWrapped();
		} else if(chunk instanceof WorldChunk c) {
			return c;
		} else {
			return null;
		}
	}

	@Override
	protected void setSynchronizing(T component) {
		component.postChange((c, v) -> sync(ComponentsInternal.SYNC_CHUNK, this.serializer, component, c, true));
	}
}
