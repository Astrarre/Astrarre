package io.github.astrarre.event.internal.core;

import java.util.Objects;

import io.github.astrarre.event.v0.api.core.SingleContextHolder;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SingleContextHolderImpl<T> implements SingleContextHolder<T> {
	protected final ThreadLocal<T> active = new ThreadLocal<>();
	protected final String name;

	public SingleContextHolderImpl(String name) {
		this.name = name;
	}

	@Override
	public void set(@NotNull T value) {
		Validate.notNull(value, "value cannot be null!");
		if (this.active.get() == null) {
			this.active.set(value);
		} else {
			throw new IllegalStateException("context holder '" + this.name + "' can only store 1 context at a time!");
		}
	}

	@Override
	public T pop(@Nullable T ref) {
		T pop = this.active.get();
		this.active.remove();
		if (ref != null && !Objects.equals(ref, pop)) {
			throw new IllegalStateException("stack corruption in '" + this.name + "' expected: " + ref + " found: " + pop);
		}
		return pop;
	}

	@Override
	public T swap(@NotNull T value) {
		Validate.notNull(value, "value cannot be null!");
		T active = this.active.get();
		this.active.set(value);
		return active;
	}

	@Override
	public void override(@NotNull T value) {
		Validate.notNull(value, "value cannot be null!");
		this.active.set(value);
	}

	@Override
	public @Nullable T getNth(int index) {
		Validate.greaterThanEqualTo(index, 0, "index must be positive!");
		if (index == 0) {
			return this.active.get();
		} else {
			return null;
		}
	}
}
