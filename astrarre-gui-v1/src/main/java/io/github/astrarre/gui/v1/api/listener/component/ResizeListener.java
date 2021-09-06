package io.github.astrarre.gui.v1.api.listener.component;

import io.github.astrarre.gui.v1.api.component.AComponent;

/**
 * Fired when a {@link AComponent#setBounds(float, float)} is called.
 */
public interface ResizeListener {
	void onResize(float width, float height);
}
