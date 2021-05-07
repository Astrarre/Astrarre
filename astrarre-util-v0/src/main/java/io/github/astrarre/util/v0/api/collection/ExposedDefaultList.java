package io.github.astrarre.util.v0.api.collection;

import java.util.AbstractList;
import java.util.List;

import org.jetbrains.annotations.Nullable;


/**
 * default list but it's protected constructors are visible
 * @param <T>
 */
public class ExposedDefaultList<T> extends AbstractList<T> {
	protected final T init;
	protected final List<T> delegate;


	public ExposedDefaultList(List<T> delegate, @Nullable T initialElement) {
		this.init = initialElement;
		this.delegate = delegate;
	}

	private boolean valid(int index) {
		return index >= 0 && index < this.delegate.size();
	}

	@Override
	public T get(int index) {
		if(this.valid(index)) {
			return this.delegate.get(index);
		}
		return null;
	}

	@Override
	public T set(int index, T element) {
		return this.delegate.set(index, element);
	}

	@Override
	public int size() {
		return this.delegate.size();
	}
}
