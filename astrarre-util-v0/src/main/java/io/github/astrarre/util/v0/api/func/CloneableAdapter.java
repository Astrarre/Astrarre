package io.github.astrarre.util.v0.api.func;

public interface CloneableAdapter<T> extends Copier<T> {
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