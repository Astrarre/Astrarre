package io.github.astrarre.event.internal.core;

import io.github.astrarre.event.v0.api.core.ContextView;
import org.jetbrains.annotations.Nullable;

public record CombinedContextView<T>(ContextView<? extends T>[] contexts) implements ContextView<T> {
	@Override
	public @Nullable T getNth(int index) {
		for (ContextView<? extends T> context : this.contexts) {
			if (index < context.size()) {
				return context.getNth(index);
			} else {
				index -= context.size();
			}
		}
		return null;
	}

	@Override
	public int size() {
		int s = 0;
		for (ContextView<? extends T> context : this.contexts) {
			s += context.size();
		}
		return s;
	}
}
