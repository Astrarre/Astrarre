package io.github.astrarre.networking.v0.api.properties;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.astrarre.networking.v0.api.ModPacketHandler;
import io.github.astrarre.networking.v0.api.SyncedProperty;
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
		ModPacketHandler.INSTANCE.registerSynchronizedClient(BLOCK_ENTITY_SYNC, (id, tag) -> {
			RegistryKey<World> key = RegistryKey.of(Registry.WORLD_KEY, Serializer.ID.read(tag, "world").to());
			BlockPos pos = FabricSerializers.BLOCK_POS.read(tag, "pos");
			int syncId = tag.getInt("syncId");
			ClientWorld world = MinecraftClient.getInstance().world;
			if (world != null && world.getRegistryKey() == key) {
				BlockEntity e = world.getBlockEntity(pos);
				if (e != null) {
					SyncedProperty<?> property = ((BlockEntityPropertyAccess) e).getProperty(syncId);
					if (property != null) {
						property.sync(tag.getValue("value"));
					}
				}
			}
		});
	}

	public final BlockEntity entity;
	public final int id;

	public BlockEntitySyncedProperty(Serializer<T> serializer, BlockEntity entity, int id) {
		super(serializer);
		this.entity = entity;
		this.id = id;
	}

	@Override
	protected void synchronize(T value) {
		World world = this.entity.getWorld();
		if (world != null && !world.isClient) {
			NBTagView.Builder tag = NBTagView.builder();
			Serializer.ID.save(tag, "world", Id.of(world.getRegistryKey().getValue()));
			tag.putInt("syncId", this.id);
			NBTagView.Builder sub = NBTagView.builder();
			this.serializer.save(sub, "value", value);
			ServerChunkManager manager = (ServerChunkManager) world.getChunkManager();
			manager.threadedAnvilChunkStorage.getPlayersWatchingChunk(new ChunkPos(this.entity.getPos()), false).forEach(s -> {
				NBTagView.Builder builder = tag.build().toBuilder();
				FabricSerializers.BLOCK_POS.save(tag, "pos", this.entity.getPos());
				ModPacketHandler.INSTANCE.sendToClient(s, BLOCK_ENTITY_SYNC, builder);
			});
		}
	}
}
