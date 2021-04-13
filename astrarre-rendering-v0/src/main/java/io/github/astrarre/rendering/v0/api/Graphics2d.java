package io.github.astrarre.rendering.v0.api;

import java.util.List;

import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.rendering.v0.edge.Stencil;
import org.jetbrains.annotations.ApiStatus;

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

	static List<OrderedText> wrap(Text text, int width) {
		return MinecraftClient.getInstance().textRenderer.wrapLines(text, width);
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

	default void tracePolygon(Polygon polygon, int color) {
		int lastVertex = polygon.vertices() - 1;
		float lastX = polygon.getX(lastVertex), lastY = polygon.getY(lastVertex);
		for (int i = 0; i < polygon.vertices(); i++) {
			float currX = polygon.getX(i), currY = polygon.getY(i);
			this.drawLine(lastX, lastY, currX, currY, color);
			lastX = currX;
			lastY = currY;
		}
	}

	/**
	 * @deprecated until I fix it
	 */
	@Deprecated
	void fillPolygon(Polygon polygon, int color);

	/**
	 * draws a sprite along the xy plane
	 * @param width how big to draw the sprite
	 */
	void drawSprite(Sprite sprite, float width, float height);

	void drawSprite(Sprite.Sized sized);

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

	default Close translate(float x, float y) {
		return this.applyTransformation(Transformation.translate(x, y, 0));
	}

	void flush();

	/**
	 * @return the stencil manager for this instance
	 */
	@ApiStatus.Experimental
	Stencil stencil();
}
