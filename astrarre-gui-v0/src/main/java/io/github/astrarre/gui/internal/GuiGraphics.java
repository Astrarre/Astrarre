package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.internal.access.Matrix4fAccess;
import io.github.astrarre.gui.v0.api.textures.Texture;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.gui.v0.api.util.Closeable;
import io.github.astrarre.gui.v0.api.Graphics2d;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
public class GuiGraphics implements Graphics2d {
	@Hide public MatrixStack matrices;
	/**
	 * offset bounds
	 */
	private float x, y, z;

	@Hide
	public GuiGraphics(MatrixStack matrices) {
		this.matrices = matrices;
	}

	@Override
	public void drawTexture(Texture texture, int x1, int y1, int x2, int y2, float x, float y) {

	}

	@Override
	public void drawHorizontalLineByLength(float x, float y, int length, int color) {
		this.drawHorizontalLine(x, y, x + length, color);
	}

	/**
	 * @implNote {@link DrawableHelper#drawHorizontalLine(MatrixStack, int, int, int, int)}
	 */
	@Override
	public void drawHorizontalLine(float startX, float y, float endX, int color) {
		if (endX < startX) {
			float i = startX;
			startX = endX;
			endX = i;
		}

		startX += this.x;
		endX += this.x;
		y += this.y;

		this.fill(startX, y, endX + 1, y + 1, color);
	}



	@Override
	public void drawVerticalLineByLength(float x, float y, int length, int color) {
		this.drawVerticalLine(x, y, y + length, color);
	}

	/**
	 * @implNote {@link DrawableHelper#drawVerticalLine(MatrixStack, int, int, int, int)}
	 */
	@Override
	public void drawVerticalLine(float x, float startY, float endY, int color) {
		if (endY < startY) {
			float i = startY;
			startY = endY;
			endY = i;
		}

		x += this.x;
		startY += this.y;
		endY += this.y;

		this.fill(x, startY + 1, x + 1, endY, color);
	}


	@Override
	public void fill(float x1, float y1, float x2, float y2, int color) {
		x1 += this.x;
		y1 += this.y;
		x2 += this.x;
		y2 += this.y;
		DrawableHelper2.fill(this.matrices.peek().getModel(), x1, y1, x2, y2, color);
	}


	@Override
	public void fillGradient(float x1, float y1, float x2, float y2, int startColor, int endColor) {
		x1 += this.x;
		y1 += this.y;
		x2 += this.x;
		y2 += this.y;
		DrawableHelper2.fillGradient(this.matrices, x1, y1, x2, y2, this.getZ(), startColor, endColor);
	}

	@Override
	public Closeable rotate(float x, float y, float degrees) {
		return this.rotateAndTranslate(x, y, degrees, 0, 0);
	}

	@Override
	public Closeable translate(float deltaX, float deltaY) {
		this.matrices.push();
		Matrix4f matrix4f = this.matrices.peek().getModel();
		Matrix4fAccess.cast(matrix4f).astrarre_addToLastColumn(deltaX, deltaY, 0);
		matrix4f.addToLastColumn(new Vector3f(deltaX, deltaY, 0));
		return () -> this.matrices.pop();
	}

	@Override
	public Closeable rotateAndTranslate(float originX, float originY, float degrees, float translateX, float translateY) {
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
	public Closeable setOffsetCloseable(float x, float y) {
		float ox1 = this.x, oy1 = this.y;
		this.setOffset(x, y);
		return () -> this.setOffset(ox1, oy1);
	}

	@Override
	public void setOffset(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public float getZ() {
		return this.z;
	}

	@Hide
	public MatrixStack getMatrices() {
		return this.matrices;
	}
}
