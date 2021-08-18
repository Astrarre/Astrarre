package io.github.astrarre.gui.v1.api.keyboard;

import java.util.Set;

import io.github.astrarre.gui.v1.api.focus.FocusHandler;

public interface KeyboardListener extends FocusHandler {
	/**
	 * Called when a user presses a key on the keyboard
	 * @param key the key the user pressed on the keyboard
	 * @param scanCode a platform-specific id for this input
	 * @param modifiers the modifiers, eg. control, shift, etc.
	 * @return true if the event was handled and should not be passed onto other handlers
	 */
	default boolean onKeyPressed(Key key, int scanCode, Set<Modifier> modifiers) {
		return false;
	}

	/**
	 * Called when a user releases a key on the keyboard
	 * @param key the key the user pressed on the keyboard
	 * @param scanCode a platform-specific id for this input
	 * @param modifiers the modifiers, eg. control, shift, etc.
	 * @return true if the event was handled and should not be passed onto other handlers
	 */
	default boolean onKeyReleased(Key key, int scanCode, Set<Modifier> modifiers) {
		return false;
	}

	/**
	 * Called for character input, this is what should be used for text boxes
	 * @param modifiers the modifiers, eg. control, shift, etc.
	 * @return true if the event was handled and should not be passed onto other handlers
	 */
	default boolean onTypedChar(Set<Modifier> modifiers, char chr) {
		return false;
	}
}
