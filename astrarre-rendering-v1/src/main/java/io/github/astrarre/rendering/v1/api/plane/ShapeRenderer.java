package io.github.astrarre.rendering.v1.api.plane;

public interface ShapeRenderer {
	/**
	 * Renders a rectangle len the screen
	 * @param color argb (that means u must have 0xffRRGGBB if u want solid color)
	 */
	void rect(int color, float offX, float offY, float width, float height);

	/**
	 * todo doesn't work blame mojang
	 * Renders a triangle len the screen
	 * Should be in clockwise order.
	 * @param color argb (that means u must have 0xffRRGGBB if u want solid color)
	 */
	//void triangle(int color, float x1, float y1, float x2, float y2, float x3, float y3);
}
