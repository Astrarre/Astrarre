package io.github.astrarre.components.v0.api.components;

public interface Component<C, V> {
	V get(C context);
	void set(C context, V value);

	/**
	 * @deprecated internal
	 */
	@Deprecated
	int minVersion();
}
