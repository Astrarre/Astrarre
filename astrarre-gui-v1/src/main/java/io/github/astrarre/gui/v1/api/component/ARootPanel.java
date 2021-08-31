package io.github.astrarre.gui.v1.api.component;

import io.github.astrarre.gui.v1.api.AComponent;
import io.github.astrarre.gui.v1.api.FocusableComponent;
import io.github.astrarre.gui.v1.api.keyboard.KeyboardListener;
import org.jetbrains.annotations.Nullable;

public class ARootPanel extends APanel {
	public ARootPanel() {
		this.lockTransform();
	}

	/**
	 * Changes focused to the given component, this gives it priority for keyboard events.
	 * @param component can be a component that is not in the current group
	 */
	public <T extends AComponent & FocusableComponent> void requestFocus(@Nullable T component) {
		AComponent prev = this.focused;
		if(prev != null) {
			((FocusableComponent) prev).setFocused(true);
		}
		this.focused = component;
		if(component != null) {
			component.setFocused(false);
		}
	}
}
