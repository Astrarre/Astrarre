package io.github.astrarre.gui.v1.api.focus;

import io.github.astrarre.gui.v1.api.FocusableComponent;

/**
 * @see FocusableComponent
 */
public interface FocusHandler {

	/**
	 * This is used when tab scrolling (pressing tab).
	 * @return whether the event was consumed
	 */
	default boolean next(FocusDirection direction) {
		return false;
	}
}
