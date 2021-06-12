package io.github.astrarre.access.v0.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

import io.github.astrarre.util.v0.api.func.IterFunc;

public final class MapFilter<T, A> {
	private final IterFunc<A> combine;
	private final A empty;
	private final Map<T, A> cached = new HashMap<>();
	private Map<T, List<A>> map;
	private final Supplier<Map<T, List<A>>> mapSupplier;

	public MapFilter(IterFunc<A> then, boolean isWeak) {
		this(then, then.combine(Collections.emptyList()), isWeak);
	}

	public MapFilter(IterFunc<A> then) {
		this(then, then.combine(Collections.emptyList()), false);
	}

	public MapFilter(IterFunc<A> then, A empty, boolean isWeak) {
		this(then, empty, isWeak ? WeakHashMap::new : HashMap::new);
	}

	public MapFilter(IterFunc<A> combine, A empty, Supplier<Map<T, List<A>>> mapSupplier) {
		this.combine = combine;
		this.empty = empty;
		this.mapSupplier = mapSupplier;
	}

	public MapFilter(IterFunc<A> then, A empty) {
		this(then, empty, false);
	}

	public boolean add(T type, A func) {
		boolean val = false;
		if (this.map == null) {
			this.map = this.mapSupplier.get();
			val = true;
		}

		List<A> list = this.map.computeIfAbsent(type, a -> new ArrayList<>());
		list.add(func);
		this.cached.put(type, this.combine.combine(list));
		return val;
	}

	public A get(T type) {
		if(this.map == null) {
			return this.empty;
		}
		return this.cached.getOrDefault(type, this.empty);
	}
}
