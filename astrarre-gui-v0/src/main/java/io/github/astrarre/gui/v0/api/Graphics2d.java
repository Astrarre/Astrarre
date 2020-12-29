package io.github.astrarre.gui.v0.api;

import io.github.astrarre.gui.v0.api.util.Closeable;
import io.github.astrarre.gui.v0.api.util.ColorUtil;
import io.github.astrarre.gui.v0.api.textures.Texture;

public interface Graphics2d {
	// todo drawTextures, drawSprite

	/**
	 * Crops the texture from (x1, y1) -> (x2, y2) and draws it at (x, y)
	 * @param texture the texture to draw
	 */
	void drawTexture(Texture texture, int x1, int y1, int x2, int y2, float x, float y);

	/**
	 * Draws a horizontal line of a given length
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 * @see ColorUtil#getARGB(int, int, int)
	 */
	void drawHorizontalLineByLength(float x, float y, int length, int color);

	/**
	 * Draws a horizontal line
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 */
	void drawHorizontalLine(float startX, float y, float endX, int color);

	/**
	 * Draws a vertical line of a given length
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 */
	void drawVerticalLineByLength(float x, float y, int length, int color);

	/**
	 * Draws a vertical line
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 */
	void drawVerticalLine(float x, float startY, float endY, int color);

	/**
	 * Fills the specified region with a given color
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 */
	void fill(float x1, float y1, float x2, float y2, int color);

	/**
	 * Fills in the specified region with a gradient which goes from the startColor (top) to the endColor (bottom)
	 *
	 * @param startColor The {@link ColorUtil#getARGB(int, int, int)} color to start from
	 * @param endColor The {@link ColorUtil#getARGB(int, int, int)} to end at
	 */
	void fillGradient(float x1, float y1, float x2, float y2, int startColor, int endColor);

	/**
	 * hint: try-with-resources are a wonderful thing
	 * @param x the x coordinate of the 'origin' of the rotation. This coordinate is added to the existing translation!
	 * @param degrees the degrees in radians
	 * @return this must be closed when you are done rendering whatever you want to rotate
	 */
	Closeable rotate(float x, float y, float degrees);

	/**
	 * hint: try-with-resources are a wonderful thing
	 * @return this must be closed when you are done rendering whatever you want to rotate (eg. rotate, then translate)
	 */
	Closeable translate(float deltaX, float deltaY);

	/**
	 * hint: try-with-resources are a wonderful thing
	 * a more efficient method of rotating, then translating
	 * @see #rotate(float, float, float)
	 * @see #translate(float, float)
	 */
	Closeable rotateAndTranslate(float originX, float originY, float degrees, float translateX, float translateY);

	/**
	 * This method returns a closable that will set the bounds to the original bounds this graphics was in when it closes, this is a utility method.
	 * hint: try-with-resources are a wonderful thing
	 */
	Closeable setOffsetCloseable(float x, float y);

	void setOffset(float x, float y);

	void setZ(float z);
	float getZ();
}
