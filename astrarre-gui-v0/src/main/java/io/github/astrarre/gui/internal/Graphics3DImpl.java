package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.internal.access.Matrix4fAccess;
import io.github.astrarre.gui.v0.api.Graphics3d;
import io.github.astrarre.gui.v0.api.textures.Texture;
import io.github.astrarre.gui.v0.api.util.Closeable;
import io.github.astrarre.stripper.Hide;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
public class Graphics3DImpl implements Graphics3d {
	@Hide public MatrixStack matrices;
	/**
	 * offset bounds
	 */
	private float x, y, z;

	@Hide
	public Graphics3DImpl(MatrixStack matrices) {
		this.matrices = matrices;
	}

	@Override
	public void drawTexture(Texture texture, int x1, int y1, int x2, int y2, float x, float y, float z) {
		MinecraftClient.getInstance().getTextureManager().bindTexture((Identifier) texture.getIdentifier());
		DrawableHelper2.drawTexture(this.matrices, x, y, z, x1, y1, x2 - x1, y2 - y1, texture.getHeight(), texture.getWidth());
	}

	@Override
	public void drawLine(float startX, float startY, float endX, float endY, int color) {
		this.fill(startX, startY, endX, endY, color);
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
		DrawableHelper2.fillGradient(this.matrices, x1, y1, x2, y2, 0, startColor, endColor);
	}

	@Override
	public Closeable rotate(float x, float y, float z, float roll, float pitch, float yaw) {
		this.matrices.push();
		this.matrices.translate(x, y, z);
		this.matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(roll));
		this.matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(pitch));
		this.matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(yaw));
		this.matrices.translate(-x, -y, -z);
		return () -> this.matrices.pop();
	}

	@Override
	public Closeable translate(float deltaX, float deltaY, float deltaZ) {
		this.matrices.push();
		Matrix4f matrix4f = this.matrices.peek().getModel();
		Matrix4fAccess.cast(matrix4f).astrarre_addToLastColumn(deltaX, deltaY, deltaZ);
		return () -> this.matrices.pop();
	}

	@Override
	public Closeable setOffsetCloseable(float x, float y, float z) {
		float ox1 = this.x, oy1 = this.y, oz1 = this.z;
		this.setOffset(x, y, z);
		return () -> this.setOffset(ox1, oy1, oz1);
	}

	@Override
	public void setOffset(float x, float y, float z) {
		this.x = x;
		this.y = y;
	}

	@Hide
	public MatrixStack getMatrices() {
		return this.matrices;
	}
}
