package io.github.astrarre.networking.v0.api;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;

/**
 * a wrapper for a property that can be synced to the client or server
 */
public abstract class SyncedProperty<T> {
	protected final Map<Behavior, Consumer<T>> listeners = new EnumMap<>(Behavior.class);
	public final Serializer<T> serializer;
	protected T value;

	public SyncedProperty(Serializer<T> serializer) {
		this.serializer = serializer;
	}

	public void set(T value) {
		this.value = value;
		this.listeners.getOrDefault(Behavior.FIRE_HOME, b -> {}).accept(value);
		this.synchronize(value);
	}

	public void setRaw(T value) {
		this.value = value;
	}

	protected abstract void synchronize(T value);

	public void onSync(Object value) {
		this.value = (T) value;
		this.listeners.getOrDefault(Behavior.FIRE_DEST, b -> {}).accept((T) value);
	}

	public void onSync(NBTagView tag, String key) {
		this.onSync(this.serializer.read(tag, key));
	}

	public T get() {
		return this.value;
	}

	/**
	 * registers a listener for the property
	 * @see Behavior
	 * @param behavior how the listener should be treated
	 * @param consumer the listener to invoke
	 */
	public void register(Behavior behavior, Consumer<T> consumer) {
		if(behavior == Behavior.FIRE_BOTH) {
			this.listeners.compute(Behavior.FIRE_HOME, (k, v) -> v == null ? consumer : v.andThen(consumer));
			this.listeners.compute(Behavior.FIRE_DEST, (k, v) -> v == null ? consumer : v.andThen(consumer));
		} else {
			this.listeners.compute(behavior, (k, v) -> v == null ? consumer : v.andThen(consumer));
		}
	}

	public enum Behavior {
		/**
		 * the listener will only be fired on the same side the property was set.
		 * For example, if you change the property on the server, the listener will only be fired on the server.
		 */
		FIRE_HOME,
		/**
		 * the listener will only be fired on the opposite side the property was set.
		 * For example, if you change the property on the server, the listener will only be fired on the client.
		 */
		FIRE_DEST,
		/**
		 * the change listener will be fired on the side the property was set, and the side the property is linked to.
		 * For example, if you change the property on the server, the listener will be fired once on the server, and once on the client when the packet arrives.
		 */
		FIRE_BOTH
	}
}