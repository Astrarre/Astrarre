package io.github.astrarre.rendering.v0.api;

import java.util.List;

import io.github.astrarre.rendering.v0.api.textures.SpriteInfo;
import io.github.astrarre.rendering.v0.api.textures.Texture;
import io.github.astrarre.rendering.v0.api.textures.TexturePart;
import io.github.astrarre.rendering.v0.api.util.Close;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public interface Graphics2d {

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

	/**
	 * @return the width, int pixels of the text
	 */
	static int getWidth(String text) {
		return MinecraftClient.getInstance().textRenderer.getWidth(text);
	}

	static int getWidth(Text text) {
		return MinecraftClient.getInstance().textRenderer.getWidth(text);
	}

	static int getWidth(OrderedText text) {
		return MinecraftClient.getInstance().textRenderer.getWidth(text);
	}

	/**
	 * Draws a line from a starting position to a specified length. (Drawn along the X axis)
	 * @param color {@link Graphics2d#getARGB(int, int, int)}
	 */
	default void drawLine(float length, int color) {
		this.drawLine(0, 0, length, 0, color);
	}

	void drawText(String text, int color, boolean shadow);

	void drawText(Text text, int color, boolean shadow);

	void drawText(OrderedText text, int color, boolean shadow);

	void drawTooltip(List<Text> text);

	void drawOrderedTooltip(List<OrderedText> text);

	void drawTooltip(ItemStack stack);

	/**
	 * draws a sprite along the xy plane
	 */
	void drawSprite(SpriteInfo sprite);

	/**
	 * Crops the texture from (x1, y1) -> (x1 + width, y1 + height) and draws it along the xy plane
	 * @param texture the texture to draw
	 */
	void drawTexture(Texture texture, int x1, int y1, int width, int height);

	default void drawTexture(TexturePart part) {
		this.drawTexture(part.texture, part.offX, part.offY, part.width, part.height);
	}

	void drawLine(float x1, float y1, float x2, float y2, int color);

	void fillRect(float x, float y, float width, float height, int color);

	/**
	 * Fills in the specified region with a gradient which goes from the startColor (top) to the endColor (bottom) along the xy plane
	 *
	 * @param startColor The {@link Graphics2d#getARGB(int, int, int)} color to start from
	 * @param endColor The {@link Graphics2d#getARGB(int, int, int)} to end at
	 */
	void fillGradient(float x, float y, float width, float height, int startColor, int endColor);

	/**
	 * Fills the specified region with a given color
	 *
	 * @param color {@link Graphics2d#getARGB(int, int, int)}
	 */
	default void fillRect(float width, float height, int color) {
		this.fillRect(0, 0, width, height, color);
	}

	default void fillGradient(float width, float height, int startColor, int endColor) {
		this.fillGradient(0, 0, width, height, startColor, endColor);
	}

	/**
	 * hint: try-with-resources
	 * @return an object, when it's closed, will return the graphics instance to the state it was in prior to the transformation
	 */
	Close applyTransformation(Transformation transformation);

	void flush();
}
