package io.github.astrarre.access.internal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.astrarre.access.v0.api.func.IterFunc;
import io.github.astrarre.access.v0.api.func.Returns;

public class MapFilter<T, A extends Returns<B>, B> {
	private final IterFunc<A> combine;
	private final A empty;
	private final Map<T, A> cached = new HashMap<>();
	private Map<T, List<A>> map;


	public MapFilter(IterFunc<A> then) {
		this.combine = then;
		this.empty = then.combine(Collections.emptyList());
	}

	public boolean add(T type, A func) {
		boolean val = false;
		if (this.map == null) {
			this.map = new HashMap<>();
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
