package io.github.astrarre.networking.v0.api.properties;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import org.jetbrains.annotations.Nullable;

/**
 * implement this on a block entity class to create new syncable properties.
 *
 * This is not completely implemented, values wont sync when the BE is loaded for the first time
 */
public interface BlockEntityPropertyAccess {
	default <T> SyncedProperty<T> newClientSyncedProperty(Serializer<T> serializer, int id) {
		throw new IllegalStateException("Mixin not applied!");
	}

	@Nullable
	default SyncedProperty<?> getProperty(int id) {
		throw new IllegalStateException("Mixin not applied!");
	}
}
