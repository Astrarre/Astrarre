package io.github.astrarre.components.v0.api.components;

import java.util.function.BiConsumer;

public interface Component<C, V> {
	V get(C context);
	void set(C context, V value);

	void addChangeListener(BiConsumer<C, V> onChange);

	/**
	 * @deprecated internal
	 */
	@Deprecated
	int minVersion();
}
