package io.github.astrarre.rendering.v0.fabric;

import io.github.astrarre.rendering.internal.util.SetupTeardown;
import io.github.astrarre.rendering.internal.util.DrawableHelper2;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.textures.Texture;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.stripper.Hide;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
public class MatrixGraphics implements FabricGraphics {

	@Hide public MatrixStack matrices;

	@Hide private SetupTeardown stage;

	@Hide
	public MatrixGraphics(MatrixStack matrices) {
		this.matrices = matrices;
	}

	@Hide
	public MatrixStack getMatrices() {
		return this.matrices;
	}

	@Override
	public void drawSprite(Sprite sprite) {
		this.pushStage(null);
		DrawableHelper.drawSprite(this.matrices, 0, 0, 0, (int) (sprite.getMaxU() - sprite.getMinU()),
				(int) (sprite.getMaxV() - sprite.getMinV()), sprite);
	}

	/**
	 * this serves as a way to avoid setting up and tearing down the same logic over and over again. For example if you call fillGradient 4 times
	 * in a
	 * row, it wont enable and disable blend 4 times in a row
	 */
	private void pushStage(@Nullable SetupTeardown stage) {
		while (this.stage != stage) {
			if (this.stage == null) {
				stage.setup();
				this.stage = stage;
				break;
			}

			this.stage.teardown();
			this.stage = this.stage.extendsFrom;
		}
	}

	@Override
	public void drawTexture(Texture texture, int x1, int y1, int x2, int y2) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture.getIdentifier());
		DrawableHelper.drawTexture(this.matrices, 0, 0, x1, y1, x2 - x1, y2 - y1, texture.getWidth(), texture.getHeight());
	}

	@Override
	public void drawLine(float length, int color) {
		this.fillRect(length, 1, color);
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
	public Close applyTransformation(Transformation transformation) {
		this.matrices.push();
		transformation.apply(this.matrices);
		return () -> this.matrices.pop();
	}
}
