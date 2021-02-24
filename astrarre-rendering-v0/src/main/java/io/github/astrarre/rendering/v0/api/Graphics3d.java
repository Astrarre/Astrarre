package io.github.astrarre.rendering.v0.api;

import io.github.astrarre.rendering.v0.api.textures.SpriteInfo;
import io.github.astrarre.rendering.v0.api.textures.Texture;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.stripper.Hide;

/**
 * Graphics3d, confusingly this is also used for GUIs because they can have layers and things can overlap
 * for GUIs, the Z axis is normal to the screen (pointing out at you) so to "roll" you actually "yaw"
 */
public interface Graphics3d {
	/**
	 * @return ARGB {@code 0xAARRGGBB} for example, red is {@code 0xFFFF0000}
	 */
	static int getARGB(int alpha, int red, int green, int blue) {
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	/**
	 * @return ARGB from RGB {@code 0xFFRRGGBB}
	 */
	static int getARGB(int red, int green, int blue) {
		return getARGB(0xff, red, green, blue);
	}

	// todo drawText, adventure text abstraction

	/**
	 * draws a sprite along the xy plane
	 */
	@Hide
	void drawSprite(SpriteInfo sprite);

	/**
	 * Crops the texture from (x1, y1) -> (x2, y2) and draws it
	 * @param texture the texture to draw
	 */
	void drawTexture(Texture texture, int x1, int y1, int x2, int y2);

	/**
	 * Draws a line from a starting position to a specified length. (Drawn along the X axis)
	 * @param color {@link Graphics3d#getARGB(int, int, int)}
	 */
	void drawLine(float length, int color);

	/**
	 * Fills the specified region with a given color
	 *
	 * @param color {@link Graphics3d#getARGB(int, int, int)}
	 */
	void fillRect(float width, float height, int color);

	/**
	 * Fills in the specified region with a gradient which goes from the startColor (top) to the endColor (bottom)
	 *
	 * @param startColor The {@link Graphics3d#getARGB(int, int, int)} color to start from
	 * @param endColor The {@link Graphics3d#getARGB(int, int, int)} to end at
	 */
	void fillGradient(float width, float height, int startColor, int endColor);

	/**
	 * This method returns a closable that will set the bounds to the original bounds this graphics was in when it closes, this is a utility method.
	 * hint: try-with-resources are a wonderful thing
	 */
	Close applyTransformation(Transformation transformation);
}
