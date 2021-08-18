package io.github.astrarre.gui.v1.api.focus;

import java.util.function.Supplier;

import io.github.astrarre.gui.v1.api.AComponent;
import org.jetbrains.annotations.Nullable;

public interface FocusHandler {
	/**
	 * This is used when tab scrolling (pressing tab).
	 * @param defaultNext The next component if this component does not override it. This supplier can be stored and called later
	 * @return the next component to focus on, it can be {@code this}
	 */
	default AComponent next(Supplier<@Nullable AComponent> defaultNext) {
		return defaultNext.get();
	}

	default boolean canFocus() {
		return true;
	}
}
