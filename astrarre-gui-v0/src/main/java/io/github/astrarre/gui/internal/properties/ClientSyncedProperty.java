package io.github.astrarre.gui.internal.properties;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.SyncedProperty;

public final class ClientSyncedProperty<T> extends SyncedProperty<T> {
	private final Drawable drawable;
	public final int id;
	public ClientSyncedProperty(Serializer<T> serializer, Drawable drawable, int id) {
		super(serializer);
		this.drawable = drawable;
		this.id = id;
	}

	@Override
	protected void synchronize(T value) {
		NBTagView.Builder tag = NBTagView.builder()
				.putInt("propertyId", this.id);
		this.serializer.save(tag, "value", value);
		this.drawable.sendToClients(Drawable.PROPERTY_SYNC, tag);
	}
}
