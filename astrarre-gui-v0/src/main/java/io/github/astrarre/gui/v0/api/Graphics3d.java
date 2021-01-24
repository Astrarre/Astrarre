package io.github.astrarre.gui.v0.api;

import io.github.astrarre.gui.v0.api.util.Closeable;
import io.github.astrarre.gui.v0.api.util.ColorUtil;
import io.github.astrarre.gui.v0.api.textures.Texture;

public interface Graphics3d {
	// todo drawTextures, drawSprite

	/**
	 * Crops the texture from (x1, y1) -> (x2, y2) and draws it at (x, y)
	 * @param texture the texture to draw
	 */
	void drawTexture(Texture texture, int x1, int y1, int x2, int y2, float x, float y, float z);

	/**
	 * Draws a line from a starting position to a specified length.
	 * @param startX the start of the Xpos
	 * @param startY the start of the Ypos
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 */
	void drawLine(float startX, float startY, float endX, float endY, int color);

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
	 * @return this must be closed when you are done rendering whatever you want to rotate
	 */
	Closeable rotate(float x, float y, float z, float roll, float pitch, float yaw);

	/**
	 * hint: try-with-resources are a wonderful thing
	 * @return this must be closed when you are done rendering whatever you want to rotate (eg. rotate, then translate)
	 */
	Closeable translate(float deltaX, float deltaY, float deltaZ);

	/**
	 * This method returns a closable that will set the bounds to the original bounds this graphics was in when it closes, this is a utility method.
	 * hint: try-with-resources are a wonderful thing
	 */
	Closeable setOffsetCloseable(float x, float y, float z);

	void setOffset(float x, float y, float z);
}
