package io.github.astrarre.components.v0.api.components.client;

public interface UnsafeClientRef<T> {
	/**
	 * @return A context that can be used as a context for a component, but nothing else
	 */
	T value();
}
