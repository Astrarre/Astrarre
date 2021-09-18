package io.github.astrarre.gui.v1.api.component;

public interface ToggleableComponent {
	default void enable() {
		this.able(true);
	}

	default void disable() {
		this.able(false);
	}

	default void able(boolean enabled) {
		((AComponent)this).set(AComponent.DISABLED, !enabled);
	}

	default boolean isEnabled() {
		return !((AComponent)this).is(AComponent.DISABLED);
	}
}
