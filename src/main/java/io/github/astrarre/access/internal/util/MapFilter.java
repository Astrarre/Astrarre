package io.github.astrarre.access.internal.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;

import io.github.astrarre.access.v0.api.func.Returns;

public class MapFilter<T, A extends Returns<B>, B> {
	private final BinaryOperator<A> andThen;
	private final A empty;
	private Map<T, A> map;

	public MapFilter(BinaryOperator<A> then, A empty) {
		this.andThen = then;
		this.empty = empty;
	}

	public boolean add(T type, A func) {
		boolean val = false;
		if (this.map == null) {
			this.map = new HashMap<>();
			val = true;
		}
		this.map.compute(type, (a1, function1) -> function1 == null ? func : this.andThen.apply(function1, func));
		return val;
	}

	public A get(T type) {
		if(this.map == null) {
			return this.empty;
		}
		return this.map.getOrDefault(type, this.empty);
	}
}
