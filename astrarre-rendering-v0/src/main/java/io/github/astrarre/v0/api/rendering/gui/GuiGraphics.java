package io.github.astrarre.v0.api.rendering.gui;

import io.github.astrarre.Hide;
import io.github.astrarre.internal.access.render.Matrix4fAccess;
import io.github.astrarre.internal.gui.GuiScreen;
import io.github.astrarre.v0.api.rendering.util.ColorUtil;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
public class GuiGraphics {
	@Hide public final GuiScreen screen;
	@Hide public MatrixStack matrices;
	/**
	 * offset bounds
	 */
	private int x, y;

	@Hide
	public GuiGraphics(MatrixStack matrices, GuiScreen screen) {
		this.matrices = matrices;
		this.screen = screen;
	}

	/**
	 * Draws a horizontal line of a given length
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 * @see ColorUtil#getARGB(int, int, int)
	 */
	public void drawHorizontalLineByLength(int x, int y, int length, int color) {
		this.drawHorizontalLine(x, y, x + length, color);
	}

	/**
	 * Draws a horizontal line
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 * @implNote {@link DrawableHelper#drawHorizontalLine(MatrixStack, int, int, int, int)}
	 */
	public void drawHorizontalLine(int startX, int y, int endX, int color) {
		if (endX < startX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		startX += this.x;
		endX += this.x;
		y += this.y;

		DrawableHelper.fill(this.matrices, startX, y, endX + 1, y + 1, color);
	}

	/**
	 * Draws a vertical line of a given length
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 */
	public void drawVerticalLineByLength(int x, int y, int length, int color) {
		this.drawVerticalLine(x, y, y + length, color);
	}

	/**
	 * Draws a vertical line
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 * @implNote {@link DrawableHelper#drawVerticalLine(MatrixStack, int, int, int, int)}
	 */
	public void drawVerticalLine(int x, int startY, int endY, int color) {
		if (endY < startY) {
			int i = startY;
			startY = endY;
			endY = i;
		}

		x += this.x;
		startY += this.y;
		endY += this.y;

		DrawableHelper.fill(this.matrices, x, startY + 1, x + 1, endY, color);
	}

	/**
	 * Fills the specified region with a given color
	 *
	 * @param color {@link ColorUtil#getARGB(int, int, int)}
	 */
	public void fill(int x1, int y1, int x2, int y2, int color) {
		x1 += this.x;
		y1 += this.y;
		x2 += this.x;
		y2 += this.y;
		DrawableHelper.fill(this.matrices, x1, y1, x2, y2, color);
	}

	/**
	 * Fills in the specified region with a gradient which goes from the startColor (top) to the endColor (bottom)
	 *
	 * @param startColor The {@link ColorUtil#getARGB(int, int, int)} color to start from
	 * @param endColor The {@link ColorUtil#getARGB(int, int, int)} to end at
	 */
	public void fillGradient(int x1, int y1, int x2, int y2, int startColor, int endColor) {
		x1 += this.x;
		y1 += this.y;
		x2 += this.x;
		y2 += this.y;
		this.screen.fillGradient(this.matrices, x1, y1, x2, y2, startColor, endColor);
	}

	/**
	 * @param x the x coordinate of the 'origin' of the rotation. This coordinate is added to the existing translation!
	 * @param degrees the degrees in radians
	 * @return this must be closed when you are done rendering whatever you want to rotate
	 */
	public Closeable rotate(int x, int y, float degrees) {
		return this.rotateAndTranslate(x, y, degrees, 0, 0);
	}

	/**
	 * @return this must be closed when you are done rendering whatever you want to rotate (eg. rotate, then translate)
	 */
	public Closeable translate(int deltaX, int deltaY) {
		this.matrices.push();
		Matrix4f matrix4f = this.matrices.peek().getModel();
		matrix4f.addToLastColumn(new Vector3f(deltaX, deltaY, 0));
		return () -> this.matrices.pop();
	}

	/**
	 * a more efficient method of rotating, then translating
	 */
	public Closeable rotateAndTranslate(int originX, int originY, float degrees, int translateX, int translateY) {
		originX += this.x;
		originY += this.y;
		translateX += this.x;
		translateY += this.y;

		this.matrices.push();
		Matrix4f matrix4f = this.matrices.peek().getModel();
		matrix4f.addToLastColumn(new Vector3f(-originX, -originY, 0));
		((Matrix4fAccess) (Object) matrix4f).astrarre_rotate(degrees, 0, 0, 1f);
		matrix4f.addToLastColumn(new Vector3f(translateX, translateY, 0));
		return () -> this.matrices.pop();
	}

	/**
	 * This method returns a closable that will set the bounds to the original bounds this graphics was in when it closes, this is a utility method.
	 */
	public Closeable setOffsetCloseable(int x, int y) {
		int ox1 = this.x, oy1 = this.y;
		this.setOffset(x, y);
		return () -> this.setOffset(ox1, oy1);
	}

	public void setOffset(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Hide
	public MatrixStack getMatrices() {
		return this.matrices;
	}

	public interface Closeable extends AutoCloseable {
		@Override
		void close();
	}
}
