package io.github.astrarre.recipe.v0.api;

import java.util.function.Consumer;

public interface ReloadableDelegate<V, R extends ReloadableDelegate<V, R>> extends Reloadable<V> {
	@Override
	default ReloadableDelegate<V, R> markClient() {
		return (ReloadableDelegate<V, R>) Reloadable.super.markClient();
	}

	@Override
	ReloadableDelegate<V, R> afterUpdate(Object anchor, Consumer<V> listener);

	@Override
	ReloadableDelegate<V, R> afterUpdate(Consumer<V> listener);

	@Override
	ReloadableDelegate<V, R> setDefault(V value);

	@Override
	V get();
}
