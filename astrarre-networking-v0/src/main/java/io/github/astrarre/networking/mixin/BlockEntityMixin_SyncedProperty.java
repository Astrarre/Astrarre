package io.github.astrarre.networking.mixin;

import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.networking.v0.api.properties.BlockEntityPropertyAccess;
import io.github.astrarre.networking.v0.api.properties.BlockEntitySyncedProperty;
import io.github.astrarre.networking.v0.api.serializer.ToPacketSerializer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.entity.BlockEntity;

@Mixin(BlockEntity.class)
public class BlockEntityMixin_SyncedProperty implements BlockEntityPropertyAccess {
	protected final Int2ObjectMap<SyncedProperty<?>> properties = new Int2ObjectOpenHashMap<>();

	@Override
	public <T> SyncedProperty<T> newClientSyncedProperty(ToPacketSerializer<T> serializer, int id) {
		SyncedProperty<T> property = new BlockEntitySyncedProperty<>(serializer, (BlockEntity) (Object)this, id);
		this.properties.put(id, property);
		return property;
	}

	@Override
	public SyncedProperty<?> getProperty(int id) {
		return this.properties.get(id);
	}
}
