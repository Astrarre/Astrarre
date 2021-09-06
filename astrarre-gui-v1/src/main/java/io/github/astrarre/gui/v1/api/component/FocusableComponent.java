package io.github.astrarre.gui.v1.api.component;

import io.github.astrarre.gui.v1.api.listener.focus.FocusDirection;
import io.github.astrarre.gui.v1.api.listener.focus.FocusHandler;

/**
 * A more useful interface for focusing on components, can also request focus {@link ARootPanel#requestFocus(AComponent)}
 */
public interface FocusableComponent extends FocusHandler {
	@Override
	default boolean next(FocusDirection direction) {
		if(this.isFocused()) {
			this.onLostFocus();
			this.setFocused(false);
			return false;
		} else {
			this.onFocused();
			this.setFocused(true);
			return true;
		}
	}

	default void onFocused() {}

	default void onLostFocus() {}

	default boolean isFocused() {
		return (((AComponent)this).flags & AComponent.FOCUSED) != 0;
	}

	default void setFocused(boolean focused) {
		if(focused) {
			((AComponent) this).flags |= AComponent.FOCUSED;
		} else {
			((AComponent) this).flags &= ~AComponent.FOCUSED;
		}
	}
}
