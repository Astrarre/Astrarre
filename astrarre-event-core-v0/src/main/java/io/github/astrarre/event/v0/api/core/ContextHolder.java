package io.github.astrarre.event.v0.api.core;

import io.github.astrarre.event.impl.core.MultiContextHolderImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Essentially a glorified {@code ThreadLocal<Stack>}
 * @param <T> the type
 */
public interface ContextHolder<T> extends ContextView<T> {
	/**
	 * Create a new context holder
	 * @param name the name of this context, used for debugging (exceptions)
	 */
	static <T> ContextHolder<T> newInstance(String name) {
		return new MultiContextHolderImpl<>(name);
	}

	/**
	 * pushes a value onto the stack
	 */
	void push(@NotNull T value);

	/**
	 * If the stack is empty, throws an exception
	 * @param ref if you have a reference to the value to pushed onto the stack, if the popped value is not {@link Object#equals(Object)}, it will throw an exception
	 * @return the popped value
	 */
	@NotNull
	T pop(@Nullable T ref);

	/**
	 * {@code
	 * T myContext = ...
	 * T swapped = context.swap(myContext); // swap context for myContext
	 * ...
	 * context.swap(swapped); // return swapped to the stack
	 * }
	 * <br>
	 * swap the top value on the stack for the passed value.
	 * if the stack is empty, pushes `value` onto the stack
	 *
	 * @param value if null, simply pops the stack
	 * @return the value that was replaced, or null
	 */
	@Nullable
	T swap(@Nullable T value);

	default boolean isEmpty() {
		return this.size() == 0;
	}
}
