package io.github.astrarre.event.v0.api.core;

import java.util.Optional;

import io.github.astrarre.event.impl.core.CombinedContextView;
import io.github.astrarre.event.impl.core.LimitedContextView;
import org.jetbrains.annotations.Nullable;

public interface ContextView<T> {
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

}
