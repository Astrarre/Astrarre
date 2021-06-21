package io.github.astrarre.event.internal.core;

import io.github.astrarre.event.v0.api.core.ContextView;
import io.github.astrarre.event.v0.api.core.SingleContextHolder;
import org.jetbrains.annotations.Nullable;

public record OrContextView<T>(SingleContextHolder<? extends T> a, SingleContextHolder<? extends T> b) implements ContextView<T> {
	@Override
	public int size() {
		return this.a.size() + this.b.size();
	}

	@Override
	public @Nullable T getNth(int index) {
		T aT = this.a.getNth(index);
		return aT == null ? this.b.getNth(index) : aT;
	}
}
