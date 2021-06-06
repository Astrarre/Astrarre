package io.github.astrarre.components.v0.api.components;

import java.util.function.BiConsumer;

import io.github.astrarre.components.v0.api.factory.ComponentManager;
import org.apache.logging.log4j.util.TriConsumer;

public interface Component<C, V> {
	V get(C context);

	void set(C context, V value);

	/**
	 * @param onChange the first value is the context, the second is the new value
	 */
	void postChange(BiConsumer<C, V> onChange);

	/**
	 * @deprecated internal
	 */
	@Deprecated
	int minVersion();

	String getMod();

	String getId();

	ComponentManager<C> getComponentManager();
}
