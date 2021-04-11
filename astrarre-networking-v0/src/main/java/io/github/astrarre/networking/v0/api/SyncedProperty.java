package io.github.astrarre.networking.v0.api;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.util.v0.api.Val;

/**
 * a wrapper for a property that can be synced to the client or server
 */
public abstract class SyncedProperty<T> extends Val<T> {
	public final Serializer<T> serializer;

	public SyncedProperty(Serializer<T> serializer) {
		this.serializer = serializer;
	}

	@Override
	public void set(T value) {
		super.set(value);
		this.synchronize(value);
	}

	public void setRaw(T value) {
		super.set(value);
	}

	protected abstract void synchronize(T value);

	protected void onSync(T value) {
		this.setRaw(value);
	}

	public void sync(NbtValue value) {
		this.onSync(this.serializer.read(value));
	}
}