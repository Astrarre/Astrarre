package io.github.astrarre.access.v0.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import io.github.astrarre.util.v0.api.func.IterFunc;

public final class MapFilter<T, A> {
	private final IterFunc<A> combine;
	private final A empty;
	private final Map<T, A> cached = new HashMap<>();
	private Map<T, List<A>> map;
	public final boolean isWeak;

	public MapFilter(IterFunc<A> then, boolean isWeak) {
		this(then, then.combine(Collections.emptyList()), isWeak);
	}

	public MapFilter(IterFunc<A> then) {
		this(then, then.combine(Collections.emptyList()), false);
	}

	public MapFilter(IterFunc<A> then, A empty, boolean isWeak) {
		this.empty = empty;
		this.isWeak = isWeak;
		this.combine = then;
	}

	public MapFilter(IterFunc<A> then, A empty) {
		this(then, empty, false);
	}

	public boolean add(T type, A func) {
		boolean val = false;
		if (this.map == null) {
			this.map = this.isWeak ? new WeakHashMap<>() : new HashMap<>();
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
