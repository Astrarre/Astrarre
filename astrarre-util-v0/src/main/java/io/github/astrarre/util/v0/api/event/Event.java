package io.github.astrarre.util.v0.api.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.astrarre.util.v0.api.func.ArrayFunc;
import io.github.astrarre.util.v0.api.func.IterFunc;

public class Event<T> {
	protected final IterFunc<T> combiner;
	protected final List<T> list = new ArrayList<>();
	protected T compiled;

	public Event(ArrayFunc<T> func, Class<T> type) {
		this(func.asIter(type));
	}

	public Event(ArrayFunc<T> func) {
		this(func.asIter());
	}

	public Event(IterFunc<T> combiner) {
		this.combiner = combiner;
	}
	
	public Event<T> addListener(T listener) {
		this.list.add(listener);
		this.compiled = null;
		return this;
	}
	
	public T get() {
		T comp = this.compiled;
		if(comp == null) {
			return this.recomp();
		}
		return comp;
	}
	
	protected T recomp() {
		return this.compiled = this.combiner.combine(Collections.unmodifiableCollection(this.list));
	}
}
