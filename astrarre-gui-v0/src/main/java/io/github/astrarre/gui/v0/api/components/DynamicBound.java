package io.github.astrarre.gui.v0.api.components;

import io.github.astrarre.gui.v0.api.util.Rect4f;

/**
 * The scale is always 'just right' for any given height and width, but not for position.
 * These bounds are not enforced in the render method, they are only used for collision in the HUD and passing events in Widget
 */
public interface DynamicBound {
	Rect4f getLocation(float screenWidth, float screenHeight);

	/**
	 * centered relative to the screen
	 */
	static DynamicBound centered(float relativeX, float relativeY, float width, float height) {
		return (screenWidth, screenHeight) -> new Rect4f((screenWidth - relativeX) / 2, (screenHeight - relativeY) / 2, width, height);
	}

	/**
	 * The origin is the center of the screen
	 * https://en.wikipedia.org/wiki/Cartesian_coordinate_system
	 */
	static DynamicBound cartesian(float x, float y, float width, float height) {
		return (screenWidth, screenHeight) -> new Rect4f(screenWidth / 2 + x, screenHeight / 2 - y, width, height);
	}
}
