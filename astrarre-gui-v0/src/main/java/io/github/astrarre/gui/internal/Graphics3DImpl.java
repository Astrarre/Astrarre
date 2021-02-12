package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.v0.api.Graphics3d;
import io.github.astrarre.gui.v0.api.Transformation;
import io.github.astrarre.gui.v0.api.textures.Texture;
import io.github.astrarre.gui.v0.api.util.Closeable;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.client.texture.Sprite;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
public class Graphics3DImpl implements Graphics3d {

	@Hide public MatrixStack matrices;

	@Hide
	public Graphics3DImpl(MatrixStack matrices) {
		this.matrices = matrices;
	}

	@Hide
	public MatrixStack getMatrices() {
		return this.matrices;
	}

	@Override
	public void drawSprite(Sprite sprite, int width, int height) {
		DrawableHelper.drawSprite(this.matrices, 0, 0, 0, width, height, (net.minecraft.client.texture.Sprite) sprite);
	}

	@Override
	public void drawTexture(Texture texture, int x1, int y1, int x2, int y2) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture.getIdentifier());
		DrawableHelper.drawTexture(this.matrices, 0, 0, x1, y1, x2 - x1, y2 - y1, texture.getWidth(), texture.getHeight());
	}

	@Override
	public void drawLine(float length, int color) {
		DrawableHelper2.fill(this.matrices.peek().getModel(), 0, 0, length, 0, color);
	}

	@Override
	public void fillRect(float width, float height, int color) {
		DrawableHelper2.fill(this.matrices.peek().getModel(), 0, 0, width, height, color);
	}

	@Override
	public void fillGradient(float width, float height, int startColor, int endColor) {
		DrawableHelper2.fillGradient(this.matrices, 0, 0, width, height, 0, startColor, endColor);
	}

	@Override
	public Closeable applyTransformation(Transformation transformation) {
		this.matrices.push();
		transformation.apply(this.matrices);
		return () -> this.matrices.pop();
	}
}
