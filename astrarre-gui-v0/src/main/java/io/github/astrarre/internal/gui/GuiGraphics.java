package io.github.astrarre.internal.gui;

import io.github.astrarre.internal.rendering.access.Matrix4fAccess;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.api.rendering.util.Closeable;
import io.github.astrarre.v0.api.rendering.Graphics2d;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
public class GuiGraphics implements Graphics2d {
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

	@Override
	public void drawHorizontalLineByLength(int x, int y, int length, int color) {
		this.drawHorizontalLine(x, y, x + length, color);
	}

	/**
	 * @implNote {@link DrawableHelper#drawHorizontalLine(MatrixStack, int, int, int, int)}
	 */
	@Override
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


	@Override
	public void drawVerticalLineByLength(int x, int y, int length, int color) {
		this.drawVerticalLine(x, y, y + length, color);
	}

	/**
	 * @implNote {@link DrawableHelper#drawVerticalLine(MatrixStack, int, int, int, int)}
	 */
	@Override
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


	@Override
	public void fill(int x1, int y1, int x2, int y2, int color) {
		x1 += this.x;
		y1 += this.y;
		x2 += this.x;
		y2 += this.y;
		DrawableHelper.fill(this.matrices, x1, y1, x2, y2, color);
	}


	@Override
	public void fillGradient(int x1, int y1, int x2, int y2, int startColor, int endColor) {
		x1 += this.x;
		y1 += this.y;
		x2 += this.x;
		y2 += this.y;
		this.screen.fillGradient(this.matrices, x1, y1, x2, y2, startColor, endColor);
	}

	@Override
	public Closeable rotate(int x, int y, float degrees) {
		return this.rotateAndTranslate(x, y, degrees, 0, 0);
	}

	@Override
	public Closeable translate(int deltaX, int deltaY) {
		this.matrices.push();
		Matrix4f matrix4f = this.matrices.peek().getModel();
		Matrix4fAccess.cast(matrix4f).astrarre_addToLastColumn(deltaX, deltaY, 0);
		matrix4f.addToLastColumn(new Vector3f(deltaX, deltaY, 0));
		return () -> this.matrices.pop();
	}

	@Override
	public Closeable rotateAndTranslate(int originX, int originY, float degrees, int translateX, int translateY) {
		originX += this.x;
		originY += this.y;
		translateX += this.x;
		translateY += this.y;

		this.matrices.push();
		Matrix4f matrix4f = this.matrices.peek().getModel();
		Matrix4fAccess access = Matrix4fAccess.cast(matrix4f);
		access.astrarre_addToLastColumn(-originX, -originY, 0);
		access.astrarre_rotate(degrees, 0, 0, 1f);
		access.astrarre_addToLastColumn(translateX, translateY, 0);
		return () -> this.matrices.pop();
	}

	@Override
	public Closeable setOffsetCloseable(int x, int y) {
		int ox1 = this.x, oy1 = this.y;
		this.setOffset(x, y);
		return () -> this.setOffset(ox1, oy1);
	}

	@Override
	public void setOffset(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void setZ(int z) {
		this.screen.setZOffset(z);
	}

	@Override
	public int getZ() {
		return this.screen.getZOffset();
	}

	@Hide
	public MatrixStack getMatrices() {
		return this.matrices;
	}
}
