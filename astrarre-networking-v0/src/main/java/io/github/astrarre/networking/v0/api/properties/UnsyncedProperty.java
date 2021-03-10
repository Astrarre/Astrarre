package io.github.astrarre.networking.v0.api.properties;

import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.networking.v0.api.serializer.ToPacketSerializer;

/**
 * doesn't sync, mostly there to be listened to
 * @param <T>
 */
public class UnsyncedProperty<T> extends SyncedProperty<T> {
	public UnsyncedProperty(ToPacketSerializer<T> serializer) {
		super(serializer);
	}

	@Override
	protected void synchronize(T value) {
	}
}
