package io.github.astrarre.gui.v0.api.components;

public interface DynamicLocation {
	Point2f getLocation(float screenWidth, float screenHeight);

	/**
	 * centered relative to the screen
	 */
	static DynamicLocation centered(float relativeX, float relativeY) {
		return (screenWidth, screenHeight) -> new Point2f((screenWidth - relativeX) / 2, (screenHeight - relativeY) / 2);
	}

	/**
	 * The origin is the center of the screen
	 * https://en.wikipedia.org/wiki/Cartesian_coordinate_system
	 */
	static DynamicLocation cartesian(float x, float y) {
		return (screenWidth, screenHeight) -> new Point2f(screenWidth / 2 + x, screenHeight / 2 - y);
	}
}
