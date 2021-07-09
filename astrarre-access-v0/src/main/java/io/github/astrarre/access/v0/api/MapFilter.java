package io.github.astrarre.access.v0.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

import io.github.astrarre.util.v0.api.func.IterFunc;

@SuppressWarnings("unchecked")
public final class MapFilter<T, F> {
	private final IterFunc<F> combine;
	private final F empty;
	private final Map<T, F> cached;
	private final Map<T, List<F>> map;

	public MapFilter(IterFunc<F> then, boolean isWeak) {
		this(then, then.combine(Collections.emptyList()), isWeak);
	}

	public MapFilter(IterFunc<F> then) {
		this(then, then.combine(Collections.emptyList()), false);
	}

	public MapFilter(IterFunc<F> then, F empty, boolean isWeak) {
		this(then, empty, isWeak ? WeakHashMap::new : HashMap::new);
	}

	public MapFilter(IterFunc<F> combine, F empty, Supplier<Map<T, ?>> mapSupplier) {
		this.combine = combine;
		this.empty = empty;
		this.map = (Map<T, List<F>>) mapSupplier.get();
		this.cached = (Map<T, F>) mapSupplier.get();
	}

	public MapFilter(IterFunc<F> then, F empty) {
		this(then, empty, false);
	}

	public boolean add(T type, F func) {
		boolean val = this.map.isEmpty();
		List<F> list = this.map.computeIfAbsent(type, a -> new ArrayList<>());
		list.add(func);
		this.cached.put(type, this.combine.combine(list));
		return val;
	}

	public Iterable<Map.Entry<T, F>> functions() {
		return this.cached.entrySet();
	}

	public int size() {
		return this.cached.size();
	}

	public F get(T type) {
		return this.cached.getOrDefault(type, this.empty);
	}
}
