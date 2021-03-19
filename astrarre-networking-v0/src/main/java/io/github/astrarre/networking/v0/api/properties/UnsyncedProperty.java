package io.github.astrarre.networking.v0.api.properties;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.networking.v0.api.SyncedProperty;

/**
 * doesn't sync, mostly there to be listened to
 * @param <T>
 */
public class UnsyncedProperty<T> extends SyncedProperty<T> {
	public UnsyncedProperty(Serializer<T> serializer) {
		super(serializer);
	}

	@Override
	protected void synchronize(T value) {
	}
}
