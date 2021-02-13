package io.github.astrarre.itemview.internal.util;

import java.util.Iterator;

public class ImmutableIterable<T> implements Iterator<T> {
	private final Iterator<T> delegate;

	public ImmutableIterable(Iterator<T> delegate) {this.delegate = delegate;}

	@Override
	public boolean hasNext() {
		return this.delegate.hasNext();
	}

	@Override
	public T next() {
		return this.delegate.next();
	}
}
