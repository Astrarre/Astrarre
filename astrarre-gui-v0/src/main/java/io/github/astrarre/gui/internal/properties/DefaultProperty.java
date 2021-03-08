package io.github.astrarre.gui.internal.properties;

import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.networking.v0.api.serializer.ToPacketSerializer;

public final class DefaultProperty<T> extends SyncedProperty<T> {

	public DefaultProperty(ToPacketSerializer<T> serializer) {
		super(serializer);
	}

	@Override
	protected void synchronize(Object value) {
		throw new UnsupportedOperationException();
	}
}
