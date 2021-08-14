package io.github.astrarre.rendering.v1.api.plane;

public interface ShapeRenderer {
	/**
	 * Renders a rectangle to the screen
	 * @param color argb color
	 */
	void rect(int color, float offX, float offY, float width, float height);

	/**
	 * Renders a triangle to the screen
	 * Should be in clockwise order.
	 * @param color argb color
	 */
	void triangle(int color, float x1, float y1, float x2, float y2, float x3, float y3);
}
