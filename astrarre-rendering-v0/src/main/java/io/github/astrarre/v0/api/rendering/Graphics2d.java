package io.github.astrarre.v0.api.rendering;

import io.github.astrarre.v0.api.rendering.util.Closeable;
import io.github.astrarre.v0.api.rendering.util.ColorUtil;

public interface Graphics2d {
	// todo drawTexture and friends

	/**
	 * Draws a horizontal line of a given length
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 * @see ColorUtil#getARGB(int, int, int)
	 */
	void drawHorizontalLineByLength(int x, int y, int length, int color);

	/**
	 * Draws a horizontal line
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 */
	void drawHorizontalLine(int startX, int y, int endX, int color);

	/**
	 * Draws a vertical line of a given length
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 */
	void drawVerticalLineByLength(int x, int y, int length, int color);

	/**
	 * Draws a vertical line
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 */
	void drawVerticalLine(int x, int startY, int endY, int color);

	/**
	 * Fills the specified region with a given color
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 */
	void fill(int x1, int y1, int x2, int y2, int color);

	/**
	 * Fills in the specified region with a gradient which goes from the startColor (top) to the endColor (bottom)
	 *
	 * @param startColor The {@link ColorUtil#getARGB(int, int, int)} color to start from
	 * @param endColor The {@link ColorUtil#getARGB(int, int, int)} to end at
	 */
	void fillGradient(int x1, int y1, int x2, int y2, int startColor, int endColor);

	/**
	 * hint: try-with-resources are a wonderful thing
	 * @param x the x coordinate of the 'origin' of the rotation. This coordinate is added to the existing translation!
	 * @param degrees the degrees in radians
	 * @return this must be closed when you are done rendering whatever you want to rotate
	 */
	Closeable rotate(int x, int y, float degrees);

	/**
	 * hint: try-with-resources are a wonderful thing
	 * @return this must be closed when you are done rendering whatever you want to rotate (eg. rotate, then translate)
	 */
	Closeable translate(int deltaX, int deltaY);

	/**
	 * hint: try-with-resources are a wonderful thing
	 * a more efficient method of rotating, then translating
	 * @see #rotate(int, int, float)
	 * @see #translate(int, int)
	 */
	Closeable rotateAndTranslate(int originX, int originY, float degrees, int translateX, int translateY);

	/**
	 * This method returns a closable that will set the bounds to the original bounds this graphics was in when it closes, this is a utility method.
	 * hint: try-with-resources are a wonderful thing
	 */
	Closeable setOffsetCloseable(int x, int y);

	void setOffset(int x, int y);

	void setZ(int z);
	int getZ();
}
