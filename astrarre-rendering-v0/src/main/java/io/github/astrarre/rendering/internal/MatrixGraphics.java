package io.github.astrarre.rendering.internal;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.rendering.internal.util.MatrixGraphicsUtil;
import io.github.astrarre.rendering.internal.util.SetupTeardown;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.textures.SpriteInfo;
import io.github.astrarre.rendering.v0.api.textures.Texture;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.rendering.v0.edge.Stencil;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
public class MatrixGraphics implements Graphics3d {
	private static final Stencil STENCIL = Stencil.newInstance();
	private final TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
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
		this.pushStage(null);
		if (shadow) {
			this.getTextRenderer().drawWithShadow(this.matrices, text, 0, 0, color);
		} else {
			this.getTextRenderer().draw(this.matrices, text, 0, 0, color);
		}
	}

	@Override
	public void drawText(Text text, int color, boolean shadow) {
		this.pushStage(null);
		if (shadow) {
			this.getTextRenderer().drawWithShadow(this.matrices, text, 0, 0, color);
		} else {
			this.getTextRenderer().draw(this.matrices, text, 0, 0, color);
		}
	}

	@Override
	public void drawText(OrderedText text, int color, boolean shadow) {
		this.pushStage(null);
		if (shadow) {
			this.getTextRenderer().drawWithShadow(this.matrices, text, 0, 0, color);
		} else {
			this.getTextRenderer().draw(this.matrices, text, 0, 0, color);
		}
	}

	@Override
	public void drawTooltip(List<Text> text) {
		this.pushStage(null);
		DummyScreen.INSTANCE.renderTooltip(this.matrices, text, 0, 0);
	}

	@Override
	public void drawOrderedTooltip(List<OrderedText> text) {
		this.pushStage(null);
		DummyScreen.INSTANCE.renderOrderedTooltip(this.matrices, text, 0, 0);
	}

	@Override
	public void drawTooltip(ItemStack stack) {
		this.pushStage(null);
		DummyScreen.INSTANCE.renderTooltip(this.matrices, stack, 0, 0);
	}

	@Override
	public void fillPolygon(Polygon polygon, int color) {
		this.pushStage(SetupTeardown.FILL);
		int a = color >> 24 & 255;
		int r = color >> 16 & 255;
		int g = color >> 8 & 255;
		int b = color & 255;
		polygon.triangleBuffer(VertexFormats.POSITION_COLOR, consumer -> consumer.color(a, r, g, b), color | Polygon.COLOR);
	}

	@Override
	public void drawSprite(SpriteInfo info) {
		this.pushStage(null);
		Sprite sprite = (Sprite) info;
		DrawableHelper
				.drawSprite(this.matrices, 0, 0, 0, (int) (sprite.getMaxU() - sprite.getMinU()), (int) (sprite.getMaxV() - sprite.getMinV()),
						sprite);
	}


	@Override
	public void drawTexture(Texture texture, float x1, float y1, float width, float height) {
		this.pushStage(null);
		this.textureManager.bindTexture(texture.getIdentifier());
		int textureWidth = texture.getWidth();
		int textureHeight = texture.getHeight();
		Matrix4f matrix = this.matrices.peek().getModel();
		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		buf.begin(7, VertexFormats.POSITION_TEXTURE);
		buf.vertex(matrix, 0f, height, 0f).texture((x1 + 0.00F) / textureWidth, (y1 + height) / textureHeight).next();
		buf.vertex(matrix, width, height, 0f).texture((x1 + width) / textureWidth, (y1 + height) / textureHeight).next();
		buf.vertex(matrix, width, 0f, 0.f).texture((x1 + width) / textureWidth, (y1 + 0.000F) / textureHeight).next();
		buf.vertex(matrix, 0f, 0f, 0.f).texture((x1 + 0.00F) / textureWidth, (y1 + 0.000F) / textureHeight).next();
		buf.end();
		BufferRenderer.draw(buf);
	}

	@Override
	public void fillGradient(float x, float y, float width, float height, int startColor, int endColor) {
		this.pushStage(SetupTeardown.FILL);
		float x2 = x + width, y2 = y + height;
		MatrixGraphicsUtil.fillGradient(this.matrices, 0, 0, 0, 0, y2, 0, x2, y2, 0, x2, 0, 0, startColor, endColor);
	}

	@Override
	public Close applyTransformation(Transformation transformation) {
		this.matrices.push();
		transformation.apply(this.matrices);
		return () -> this.matrices.pop();
	}

	@Override
	public void flush() {
		this.pushStage(null);
	}

	@Override
	public Stencil stencil() {
		return STENCIL;
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

	public TextRenderer getTextRenderer() {
		TextRenderer textRenderer = this.textRenderer;
		if (textRenderer == null) {
			textRenderer = this.textRenderer = MinecraftClient.getInstance().textRenderer;
		}
		return textRenderer;
	}

	@Override
	public void drawItem(ItemKey stack) {
		this.pushStage(null);
		this.getItemRenderer().zOffset = 0;
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(this.matrices.peek().getModel());
		RenderSystem.translatef(0, 0, -150);
		this.getItemRenderer().renderInGui(stack.createItemStack(1), 1, 1);
		RenderSystem.popMatrix();
	}

	@Override
	public void drawItem(ItemStack stack) {
		//this.renderGuiItemModel(itemStack, x, y, this.getHeldItemModel(itemStack, (World)null, entity))
		this.pushStage(null);
		this.getItemRenderer().zOffset = 0;
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(this.matrices.peek().getModel());
		RenderSystem.translatef(0, 0, -150);
		this.getItemRenderer().renderInGui(stack, 1, 1);
		RenderSystem.pushMatrix();
		this.getItemRenderer().renderGuiItemOverlay(this.getTextRenderer(), stack, 1, 1);
		RenderSystem.popMatrix();
		RenderSystem.popMatrix();
	}

	@Override
	public void drawLine(float x1, float y1, float z1, float x2, float y2, float z2, int color) {
		this.pushStage(SetupTeardown.FILL);
		Matrix4f matrix = this.matrices.peek().getModel();
		int a = color >> 24 & 255;
		int r = color >> 16 & 255;
		int g = color >> 8 & 255;
		int b = color & 255;
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, x1, y1, x1).color(r, g, b, a).next();
		bufferBuilder.vertex(matrix, x2, y2, z2).color(r, g, b, a).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}

	@Override
	public void fillRect(float x1,
			float y1,
			float z1,
			float x2,
			float y2,
			float z2,
			float x3,
			float y3,
			float z3,
			float x4,
			float y4,
			float z4,
			int color) {
		this.pushStage(SetupTeardown.FILL);
		MatrixGraphicsUtil.fill(this.matrices.peek().getModel(), x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, color);
	}

	public ItemRenderer getItemRenderer() {
		ItemRenderer itemRenderer = this.itemRenderer;
		if (itemRenderer == null) {
			itemRenderer = this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
		}
		return itemRenderer;
	}
}
