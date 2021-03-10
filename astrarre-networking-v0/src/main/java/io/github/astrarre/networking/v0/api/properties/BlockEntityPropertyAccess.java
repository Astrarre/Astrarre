package io.github.astrarre.networking.v0.api.properties;

import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.networking.v0.api.serializer.ToPacketSerializer;
import org.jetbrains.annotations.Nullable;

/**
 * implement this on a block entity class to create new syncable properties
 */
public interface BlockEntityPropertyAccess {
	default <T> SyncedProperty<T> newClientSyncedProperty(ToPacketSerializer<T> serializer, int id) {
		throw new IllegalStateException("Mixin not applied!");
	}

	@Nullable
	default SyncedProperty<?> getProperty(int id) {
		throw new IllegalStateException("Mixin not applied!");
	}
}
