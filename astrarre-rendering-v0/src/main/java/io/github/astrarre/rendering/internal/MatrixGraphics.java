package io.github.astrarre.rendering.internal;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.rendering.internal.util.MatrixGraphicsUtil;
import io.github.astrarre.rendering.internal.util.SetupTeardown;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.textures.SpriteInfo;
import io.github.astrarre.rendering.v0.api.textures.Texture;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
public class MatrixGraphics implements Graphics3d {
	public MatrixStack matrices;
	private TextRenderer textRenderer;
	private ItemRenderer itemRenderer;

	private SetupTeardown stage;

	public MatrixGraphics(MatrixStack matrices) {
		this.matrices = matrices;
	}

	public MatrixStack getMatrices() {
		return this.matrices;
	}

	@Override
	public void drawText(String text, int color, boolean shadow) {
		if (shadow) {
			this.getTextRenderer().drawWithShadow(this.matrices, text, 0, 0, color);
		} else {
			this.getTextRenderer().draw(this.matrices, text, 0, 0, color);
		}
	}

	public TextRenderer getTextRenderer() {
		TextRenderer textRenderer = this.textRenderer;
		if (textRenderer == null) {
			textRenderer = this.textRenderer = MinecraftClient.getInstance().textRenderer;
		}
		return textRenderer;
	}

	@Override
	public void drawText(Text text, int color, boolean shadow) {
		if (shadow) {
			this.getTextRenderer().drawWithShadow(this.matrices, text, 0, 0, color);
		} else {
			this.getTextRenderer().draw(this.matrices, text, 0, 0, color);
		}
	}

	@Override
	public void drawText(OrderedText text, int color, boolean shadow) {
		if (shadow) {
			this.getTextRenderer().drawWithShadow(this.matrices, text, 0, 0, color);
		} else {
			this.getTextRenderer().draw(this.matrices, text, 0, 0, color);
		}
	}

	@Override
	public void drawSprite(SpriteInfo info) {
		this.pushStage(null);
		Sprite sprite = (Sprite) info;
		DrawableHelper
				.drawSprite(this.matrices, 0, 0, 0, (int) (sprite.getMaxU() - sprite.getMinU()), (int) (sprite.getMaxV() - sprite.getMinV()),
						sprite);
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
	public void drawTexture(Texture texture, int x1, int y1, int width, int height) {
		Validate.positive(width, "Width cannot be negative!");
		Validate.positive(height, "Height cannot be negative!");
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture.getIdentifier());
		DrawableHelper.drawTexture(this.matrices, 0, 0, x1, y1, width, height, texture.getWidth(), texture.getHeight());
	}

	@Override
	public void drawLine(float x1, float y1, float x2, float y2, int color) {
		MatrixGraphicsUtil.line(this.matrices, x1, x2, y1, y2, color);
	}

	@Override
	public void drawItem(ItemKey stack) {
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(this.matrices.peek().getModel());
		this.getItemRenderer().renderInGui(stack.createItemStack(1), 1, 1);
		RenderSystem.popMatrix();
	}

	@Override
	public void drawItem(ItemStack stack) {
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(this.matrices.peek().getModel());
		this.getItemRenderer().renderGuiItemOverlay(this.getTextRenderer(), stack, 0, 0);
		this.getItemRenderer().renderInGui(stack, 1, 1);
		RenderSystem.popMatrix();
	}

	public ItemRenderer getItemRenderer() {
		ItemRenderer itemRenderer = this.itemRenderer;
		if (itemRenderer == null) {
			itemRenderer = this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
		}
		return itemRenderer;
	}

	@Override
	public void drawLine(float length, int color) {
		this.fillRect(length, 1, color);
	}

	@Override
	public void fillRect(float x, float y, float width, float height, int color) {
		MatrixGraphicsUtil.fill(this.matrices.peek().getModel(), x, y, x + width, y + height, color);
	}

	@Override
	public void fillRect(float width, float height, int color) {
		MatrixGraphicsUtil.fill(this.matrices.peek().getModel(), 0, 0, width, height, color);
	}

	@Override
	public void fillGradient(float width, float height, int startColor, int endColor) {
		MatrixGraphicsUtil.fillGradient(this.matrices, 0, 0, width, height, 0, startColor, endColor);
	}

	@Override
	public Close applyTransformation(Transformation transformation) {
		this.matrices.push();
		transformation.apply(this.matrices);
		return () -> this.matrices.pop();
	}
}
