package io.github.astrarre.components.v0.api;

public interface Copier<T> {
	Copier<String> STRING = immutable();

	static <T> Copier<T> clonable(ClonableAdapter<T> adapter) {
		return adapter;
	}

	static <T> Copier<T> immutable() {
		return t -> t;
	}

	T copy(T val);
}
