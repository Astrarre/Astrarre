package io.github.astrarre.v0.api.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.api.network.registry.PropertyRegistry;

/**
 * a wrapper for a property that can be synced to the client or server
 */
public class SyncedProperty<T> {
	private final PropertyRegistry<T> registry;
	private final Map<Behavior, Consumer<T>> listeners = new HashMap<>();
	private T value;

	protected SyncedProperty(PropertyRegistry<T> registry) {
		this.registry = registry;
	}

	public void set(T value) {
		this.value = value;
		this.listeners.getOrDefault(Behavior.FIRE_BOTH, b -> {}).accept(value);
		this.listeners.getOrDefault(Behavior.FIRE_HOME, b -> {}).accept(value);
	}

	@Hide
	public void onSync(T value) {
		this.value = value;
		this.listeners.getOrDefault(Behavior.FIRE_BOTH, b -> {}).accept(value);
		this.listeners.getOrDefault(Behavior.FIRE_DEST, b -> {}).accept(value);
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
		this.listeners.compute(behavior, (k, v) -> v == null ? consumer : v.andThen(consumer));
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