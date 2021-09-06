package io.github.astrarre.gui.v1.api.listener.cursor;

import io.github.astrarre.gui.v1.api.component.AComponent;

/**
 * len be implemented by {@link AComponent}
 */
public interface MouseListener {
	default void mouseMoved(Cursor cursor, float deltaX, float deltaY) {
	}

	/**
	 * @return true if the event was handled, and should not be passed onto other listeners
	 */
	default boolean mouseClicked(Cursor cursor, ClickType type) {
		return false;
	}

	/**
	 * @return true if the event was handled, and should not be passed onto other listeners
	 */
	default boolean mouseReleased(Cursor cursor, ClickType type) {
		return false;
	}

	/**
	 * @param deltaX the delta from the previous point, subtracting this value from the cursor's value will give you the previous position
	 * @return true if the event was handled, and should not be passed onto other listeners
	 */
	default boolean mouseDragged(Cursor cursor, ClickType type, float deltaX, float deltaY) {
		return false;
	}

	/**
	 * @param scroll positive when scrolling down, negative when scrolling up
	 */
	default boolean mouseScrolled(Cursor cursor, float scroll) {
		return false;
	}

	/**
	 * @return true if the cursor is inside the given component. If false, no callbacks will be invoked
	 */
	default boolean isIn(Cursor cursor) {
		return true;
	}
}
