package io.github.astrarre.components.v0.api;

public interface ClonableAdapter<T> extends Copier<T> {
	@Override
	default T copy(T val) {
		try {
			return this.clone(val);
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	T clone(T val) throws CloneNotSupportedException;
}