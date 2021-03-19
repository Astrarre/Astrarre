package io.github.astrarre.gui.internal.properties;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.networking.v0.api.SyncedProperty;

public final class DefaultProperty<T> extends SyncedProperty<T> {
	public DefaultProperty(Serializer<T> serializer) {
		super(serializer);
	}

	@Override
	protected void synchronize(Object value) {
		throw new UnsupportedOperationException();
	}
}
