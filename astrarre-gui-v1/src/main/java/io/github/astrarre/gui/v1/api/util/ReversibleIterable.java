package io.github.astrarre.gui.v1.api.util;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface ReversibleIterable<T> extends Iterable<T> {
	static <T> ReversibleIterable<T> list(List<T> list) {
		return () -> ReversibleIterator.list(list);
	}

	static <T> ReversibleIterable<T> iter(Iterable<T> iterable) {
		return () -> ReversibleIterator.iter(iterable);
	}



	@NotNull
	@Override
	ReversibleIterator<T> iterator();
}
