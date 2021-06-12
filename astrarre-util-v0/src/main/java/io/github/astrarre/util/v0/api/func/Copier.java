package io.github.astrarre.util.v0.api.func;

public interface Copier<T> {
	static <T> Copier<T> clonable(CloneableAdapter<T> adapter) {
		return adapter;
	}

	static <T> Copier<T> immutable() {
		return t -> t;
	}

	T copy(T val);
}
