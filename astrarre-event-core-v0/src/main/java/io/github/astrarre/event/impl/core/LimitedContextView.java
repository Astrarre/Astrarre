package io.github.astrarre.event.impl.core;

import io.github.astrarre.event.v0.api.core.ContextView;
import org.jetbrains.annotations.Nullable;

public final record LimitedContextView<T>(int offset, int limit, ContextView<T> view) implements ContextView<T> {
	@Override
	public @Nullable T getNth(int index) {
		if (index < this.limit) {
			return this.view.getNth(this.limit + this.offset);
		} else {
			return null;
		}
	}

	@Override
	public int size() {
		return Math.min(this.limit, this.view.size());
	}
}
