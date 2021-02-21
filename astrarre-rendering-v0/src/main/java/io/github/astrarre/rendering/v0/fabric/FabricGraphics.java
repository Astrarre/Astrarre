package io.github.astrarre.rendering.v0.fabric;

import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.textures.Texture;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.util.v0.api.ColorUtil;

import net.minecraft.client.texture.Sprite;

/**
 * Graphics3d, confusingly this is also used for GUIs because they can have layers and things can overlap
 * for GUIs, the Z axis is normal to the screen (pointing out at you) so to "roll" you actually "yaw"
 */
public interface FabricGraphics {
	// todo drawText, text abstraction :ohno:

	/**
	 * draws a sprite along the xy plane
	 */
	@Hide
	void drawSprite(Sprite sprite);

	/**
	 * Crops the texture from (x1, y1) -> (x2, y2) and draws it
	 * @param texture the texture to draw
	 */
	void drawTexture(Texture texture, int x1, int y1, int x2, int y2);

	/**
	 * Draws a line from a starting position to a specified length. (Drawn along the X axis)
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 */
	void drawLine(float length, int color);

	/**
	 * Fills the specified region with a given color
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 */
	void fillRect(float width, float height, int color);

	/**
	 * Fills in the specified region with a gradient which goes from the startColor (top) to the endColor (bottom)
	 *
	 * @param startColor The {@link ColorUtil#getARGB(int, int, int)} color to start from
	 * @param endColor The {@link ColorUtil#getARGB(int, int, int)} to end at
	 */
	void fillGradient(float width, float height, int startColor, int endColor);

	/**
	 * This method returns a closable that will set the bounds to the original bounds this graphics was in when it closes, this is a utility method.
	 * hint: try-with-resources are a wonderful thing
	 */
	Close applyTransformation(Transformation transformation);
}
