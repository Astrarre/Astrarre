package io.github.astrarre.gui.v1.api.cursor;

import io.github.astrarre.gui.v1.api.AComponent;

/**
 * to be implemented by {@link AComponent}
 */
public interface MouseListener {
	default void onMouseMoved(Cursor cursor, double mousePrevX, double mousePrevY) {
	}

	/**
	 * @return true if the event was handled, and should not be passed onto other listeners
	 */
	default boolean onMouseClicked(Cursor cursor, ClickType type) {
		return false;
	}

	/**
	 * @return true if the event was handled, and should not be passed onto other listeners
	 */
	default boolean onMouseReleased(Cursor cursor, ClickType type) {
		return false;
	}

	/**
	 * @return true if the event was handled, and should not be passed onto other listeners
	 */
	default boolean onMouseDragged(Cursor cursor, ClickType type, double deltaX, double deltaY) {
		return false;
	}

	/**
	 * @param scroll positive when scrolling down, negative when scrolling up
	 */
	default boolean onMouseScrolled(Cursor cursor, double scroll) {
		return false;
	}

	/**
	 * @return true if the cursor is inside the given component. If false, no callbacks will be invoked
	 */
	default boolean isIn(Cursor cursor) {
		return true;
	}
}
