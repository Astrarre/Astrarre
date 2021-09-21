package io.github.astrarre.components.v0.fabric.builder;

import java.io.IOException;
import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.factory.ComponentManager;
import io.github.astrarre.components.v0.fabric.FabricByteSerializer;
import io.github.astrarre.components.v0.fabric.FabricComponents;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.netty.buffer.Unpooled;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class BlockEntityComponentBuilder<V, T extends Component<BlockEntity, V>> extends SerializableComponentBuilder<BlockEntity, V, T> {
	protected BlockEntityComponentBuilder(ComponentManager<BlockEntity> manager,
			Map<String, Pair<Component<BlockEntity, ?>, FabricByteSerializer<?>>> synchronize,
			Map<String, Pair<Component<BlockEntity, ?>, Serializer<?>>> internal) {
		super(manager, synchronize, internal);
	}

	public static <V, T extends Component<BlockEntity, V>> BlockEntityComponentBuilder<V, T> blockEntity() {
		return new BlockEntityComponentBuilder<>(
				ComponentsInternal.BLOCK_ENTITY_MANAGER,
				ComponentsInternal.SYNC_BLOCK_ENTITY_INTERNAL,
				ComponentsInternal.SERIALIZE_BLOCK_ENTITY_INTERNAL);
	}

	@Override
	protected void setSynchronizing(T component) {
		component.postChange((c, v) -> sync(ComponentsInternal.SYNC_BLOCK_ENTITY, this.serializer, component, c, true));
	}

	public static <V> CustomPayloadS2CPacket sync(Identifier packetId, FabricByteSerializer<V> serializer, Component<BlockEntity, V> component, BlockEntity context, boolean send) {
		return syncWorldBased(packetId, serializer, component, context, send, BlockEntity::getWorld, (entity, packet) -> {
			ServerChunkManager manager = ((ServerWorld)entity.getWorld()).getChunkManager();
			manager.threadedAnvilChunkStorage.getPlayersWatchingChunk(new ChunkPos(context.getPos()), false)
					.forEach(e -> e.networkHandler.sendPacket(packet));
		}, (entity, buf) -> {
			buf.writeString(Registry.BLOCK_ENTITY_TYPE.getId(context.getType()).toString());
			buf.writeBlockPos(context.getPos());
		});
	}
}
