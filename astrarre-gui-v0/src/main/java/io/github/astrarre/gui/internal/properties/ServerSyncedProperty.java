package io.github.astrarre.gui.internal.properties;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.SyncedProperty;

public final class ServerSyncedProperty<T> extends SyncedProperty<T> {
	private final ADrawable drawable;
	private final int id;
	public ServerSyncedProperty(Serializer<T> serializer, ADrawable drawable, int id) {
		super(serializer);
		this.drawable = drawable;
		this.id = id;
	}

	@Override
	protected void synchronize(T value) {
		NBTagView.Builder tag = NBTagView.builder()
				                        .putInt("propertyId", this.id);
		this.serializer.save(tag, "value", value);
		this.drawable.sendToServer(ADrawable.PROPERTY_SYNC, tag);
	}
}
