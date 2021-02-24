package io.github.astrarre.util.v0.api.collection;

import java.util.Iterator;

/**
 * iterator over 2 elements, if they're both the same element it just iterates through the first one
 * @param <T>
 */
public class DualIterator<T> implements Iterator<T> {
	private T current;
	private final T b;

	public DualIterator(T a, T b) {
		this.current = a;
		this.b = b;
	}

	@Override
	public boolean hasNext() {
		return this.current != null;
	}

	@Override
	public T next() {
		T current = this.current;
		this.current = current.equals(this.b) ? null : this.b;
		return current;
	}
}
