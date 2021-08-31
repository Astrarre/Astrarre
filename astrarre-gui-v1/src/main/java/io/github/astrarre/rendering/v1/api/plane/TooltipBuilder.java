package io.github.astrarre.rendering.v1.api.plane;

import io.github.astrarre.gui.v1.api.component.icon.Icon;

/**
 * @see #render()
 */
public interface TooltipBuilder {
	TooltipBuilder add(Icon icon);

	/**
	 * Each render call to this text renderer will add another component to the tooltip builder
	 */
	TextRenderer textRenderer(int color, boolean shadow);

	/**
	 * Actually renders the tooltip, this must be called atleast once!
	 */
	void render();

	int currentWidth();

	int currentHeight();
}
