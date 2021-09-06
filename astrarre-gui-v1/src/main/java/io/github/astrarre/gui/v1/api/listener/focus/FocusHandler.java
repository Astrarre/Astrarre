package io.github.astrarre.gui.v1.api.listener.focus;

import io.github.astrarre.gui.v1.api.component.FocusableComponent;

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
