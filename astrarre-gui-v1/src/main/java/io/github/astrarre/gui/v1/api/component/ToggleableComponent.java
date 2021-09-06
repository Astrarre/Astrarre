package io.github.astrarre.gui.v1.api.component;

public interface ToggleableComponent {
	default void enable() {
		((AComponent)this).set(AComponent.DISABLED, false);
	}

	default void disable() {
		((AComponent)this).set(AComponent.DISABLED, true);
	}

	default void able(boolean enabled) {
		((AComponent)this).set(AComponent.DISABLED, !enabled);
	}

	default boolean isEnabled() {
		return !((AComponent)this).is(AComponent.DISABLED);
	}
}
