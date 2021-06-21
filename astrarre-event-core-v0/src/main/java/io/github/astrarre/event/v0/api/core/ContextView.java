package io.github.astrarre.event.v0.api.core;

import java.util.Iterator;
import java.util.Optional;

import io.github.astrarre.event.internal.core.CombinedContextView;
import io.github.astrarre.event.internal.core.LimitedContextView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ContextView<T> extends Iterable<T> {
	/**
	 * combine multiple views into one
	 */
	@SafeVarargs
	static <T> ContextView<T> combine(ContextView<? extends T>... contexts) {
		return new CombinedContextView<>(contexts);
	}

	/**
	 * the number of values currently on the stack
	 */
	int size();

	@Nullable
	default T getFirst() {
		return this.getNth(0);
	}

	/**
	 * get the nth value on the stack. Essentially a peek operation
	 */
	@Nullable T getNth(int index);

	@Nullable
	default <C extends T> C findFirst(Class<C> type) {
		for (T t : this) {
			if(type.isInstance(t)) {
				return (C) t;
			}
		}
		return null;
	}

	default Optional<T> getFirstOpt() {
		return this.getNthOpt(0);
	}

	default Optional<T> getNthOpt(int index) {
		return Optional.ofNullable(this.getNth(index));
	}

	/**
	 * a limited view of a context, everything past the `limit`th context is unreachable
	 */
	default ContextView<T> limitedView(int offset, int limit) {
		return new LimitedContextView<>(offset, limit, this);
	}

	@NotNull
	@Override
	default Iterator<T> iterator() {
		return new Iterator<>() {
			int i = 0;

			@Override
			public boolean hasNext() {
				return ContextView.this.getNth(this.i) != null;
			}

			@Override
			public T next() {
				return ContextView.this.getNth(this.i++);
			}
		};
	}
}
