package io.github.astrarre.networking.v0.api.properties;

import io.github.astrarre.networking.v0.api.ModPacketHandler;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.networking.v0.api.serializer.ToPacketSerializer;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class BlockEntitySyncedProperty<T> extends SyncedProperty<T> {
	public static final Id BLOCK_ENTITY_SYNC = Id.create("astrarre-networking-v0", "block_entity_sync");

	static {
		ModPacketHandler.INSTANCE.registerSynchronizedClient(BLOCK_ENTITY_SYNC, (id, buf) -> {
			RegistryKey<World> key = RegistryKey.of(Registry.DIMENSION, buf.readId().to());
			BlockPos pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
			int syncId = buf.readInt();
			ClientWorld world = MinecraftClient.getInstance().world;
			if (world != null && world.getRegistryKey() == key) {
				BlockEntity e = world.getBlockEntity(pos);
				if (e != null) {
					SyncedProperty<?> property = ((BlockEntityPropertyAccess) e).getProperty(syncId);
					if (property != null) {
						property.onSync(property.serializer.read(buf));
					}
				}
			}
		});
	}

	public final BlockEntity entity;
	public final int id;

	public BlockEntitySyncedProperty(ToPacketSerializer<T> serializer, BlockEntity entity, int id) {
		super(serializer);
		this.entity = entity;
		this.id = id;
	}

	@Override
	protected void synchronize(T value) {
		World world = this.entity.getWorld();
		if (world != null && !world.isClient) {
			ServerChunkManager manager = (ServerChunkManager) world.getChunkManager();
			manager.threadedAnvilChunkStorage.getPlayersWatchingChunk(new ChunkPos(this.entity.getPos()), false)
					.forEach(s -> ModPacketHandler.INSTANCE.sendToClient(s, BLOCK_ENTITY_SYNC, output -> {
						output.writeId(Id.of(world.getRegistryKey().getValue()));
						BlockPos pos = this.entity.getPos();
						output.writeInt(pos.getX());
						output.writeInt(pos.getY());
						output.writeInt(pos.getZ());
						output.writeInt(this.id);
						this.serializer.write(output, value);
					}));
		}
	}
}
