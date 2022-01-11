package io.github.astrarre.recipe.v0.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.astrarre.recipe.internal.ReloadableInternals;

@SuppressWarnings("unchecked")
public interface ReloadableList<V> extends ReloadableDelegate<List<V>, ReloadableList<V>>, List<V> {
	default <N> ReloadableList<N> mapAll(Function<V, N> mappingFunction, Reloadable<?>... dependencies) {
		return ReloadableInternals.delegateType(
				this.map(list -> list.stream().map(mappingFunction).collect(Collectors.toCollection(ArrayList::new)), dependencies),
				List.class);
	}

	default ReloadableList<V> immutable() {
		return ReloadableInternals.delegateType(
				this.map(List::copyOf),
				List.class
		);
	}
}