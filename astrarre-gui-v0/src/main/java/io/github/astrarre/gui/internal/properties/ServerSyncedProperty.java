package io.github.astrarre.gui.internal.properties;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.networking.v0.api.serializer.ToPacketSerializer;

public final class ServerSyncedProperty<T> extends SyncedProperty<T> {
	private final Drawable drawable;
	private final int id;
	public ServerSyncedProperty(ToPacketSerializer<T> serializer, Drawable drawable, int id) {
		super(serializer);
		this.drawable = drawable;
		this.id = id;
	}

	@Override
	protected void synchronize(T value) {
		this.drawable.sendToServer(Drawable.PROPERTY_SYNC, output -> {
			output.writeInt(this.id);
			this.serializer.write(output, value);
		});
	}
}
