package io.github.astrarre.event.v0.api.core;

import io.github.astrarre.event.internal.core.OrContextView;
import io.github.astrarre.event.internal.core.SingleContextHolderImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Essentially a glorified ThreadLocal
 */
public interface SingleContextHolder<T> extends ContextView<T> {
	static <T> ContextView<T> or(SingleContextHolder<? extends T> a, SingleContextHolder<? extends T> b) {
		return new OrContextView<>(a, b);
	}

	/**
	 * Create a new context holder that can only store 1 value.
	 * @param name the name of this context, used for debugging (exceptions)
	 */
	static <T> SingleContextHolder<T> newInstance(String name) {
		return new SingleContextHolderImpl<>(name);
	}

	/**
	 * sets the top value on the stack, throws if already occupied.
	 */
	void set(@NotNull T value);

	/**
	 * swap the top value on the stack for the passed value. This can be used to temporary change the value in the holder.
	 */
	@Nullable
	T swap(@Nullable T value);

	/**
	 * If the stack is empty, throws an exception
	 * @param ref if you have a reference to the value to pushed onto the stack, if the popped value is not {@link Object#equals(Object)}, it will throw an exception
	 * @return the popped value
	 */
	T pop(@Nullable T ref);

	default boolean isEmpty() {
		return this.getFirst() == null;
	}

	@Override
	default int size() {
		return this.isEmpty() ? 0 : 1;
	}

}
