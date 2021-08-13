package io.github.astrarre.rendering.v1.api.plane;

import java.util.List;

import io.github.astrarre.rendering.v1.api.util.AngleFormat;
import io.github.astrarre.util.v0.api.Edge;
import io.github.astrarre.util.v0.api.SafeCloseable;

import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

/**
 * todo make a transform builder or something, since we need to allocate an object for the closeable anyways
 * basically you'd go
 * <code>
 * try(renderer.translate(10, 10).rotate(10).scale(104)) {
 *     // ...
 * }
 * </code>
 */
public interface Render2D {
	/**
	 * all render calls between this and when the closable is closed will have the given transformation applied to it.
	 * Transformations do stack
	 */
	SafeCloseable transform(Transform2D transform);

	SafeCloseable translate(float offX, float offY);

	SafeCloseable scale(float scaleX, float scaleY);

	SafeCloseable rotate(AngleFormat format, float theta);

	ShapeRenderer fill();

	ShapeRenderer outline();

	TextRenderer text(int color, float x, float y, boolean shadow);

	/**
	 * @param color argb color
	 */
	void line(int color, float x1, float y1, float x2, float y2); // todo add width?

	default void deltaLine(int color, float x1, float y1, float dX, float dY) {
		this.line(color, x1, y1, x1 + dX, y1 + dY);
	}

	/**
	 * @param texture the texture
	 * @param width how big to draw the texture itself
	 */
	void texture(Texture texture, float offX, float offY, float width, float height);

	void flush();
}
