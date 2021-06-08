package io.github.astrarre.rendering.v0.fabric;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.rendering.internal.util.MatrixGraphicsUtil;
import io.github.astrarre.rendering.internal.util.SetupTeardown;
import io.github.astrarre.rendering.v0.api.Graphics2d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.rendering.v0.edge.Stencil;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

public class FabricGraphics2d implements Graphics2d, FabricGraphics {
	private static final Stencil STENCIL = Stencil.newInstance();
	// todo custom buffer builder
	private final TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
	public final MatrixStack matrices;
	protected final Close pop;
	private TextRenderer textRenderer;
	private ItemRenderer itemRenderer;
	private SetupTeardown stage;

	public FabricGraphics2d(MatrixStack matrices) {
		this.matrices = matrices;
		this.pop = this.matrices::pop;
	}

	@Override
	public void drawText(String text, int color, boolean shadow) {
		this.pushStage(null);
		if (shadow) {
			this.getTextRenderer().drawWithShadow(this.matrices, text, 0, 0, color);
		} else {
			this.getTextRenderer().draw(this.matrices, text, 0, 0, color);
		}
		RenderSystem.enableDepthTest();
	}

	@Override
	public void drawText(Text text, int color, boolean shadow) {
		this.pushStage(null);
		if (shadow) {
			this.getTextRenderer().drawWithShadow(this.matrices, text, 0, 0, color);
		} else {
			this.getTextRenderer().draw(this.matrices, text, 0, 0, color);
		}
		RenderSystem.enableDepthTest();
	}

	@Override
	public void drawText(OrderedText text, int color, boolean shadow) {
		this.pushStage(null);
		if (shadow) {
			this.getTextRenderer().drawWithShadow(this.matrices, text, 0, 0, color);
		} else {
			this.getTextRenderer().draw(this.matrices, text, 0, 0, color);
		}
		RenderSystem.enableDepthTest();
	}


	@Override
	public void fillPolygon(Polygon polygon, int color) {
		this.pushStage(SetupTeardown.FILL);
		int a = (color >> 24) & 255;
		int r = (color >> 16) & 255;
		int g = (color >> 8) & 255;
		int b = color & 255;
		BufferBuilder builder = polygon.triangleBuffer(this.matrices, VertexFormats.POSITION_COLOR, consumer -> consumer.color(a, r, g, b));
		BufferRenderer.draw(builder);
	}

	@Override
	public void drawSprite(Sprite sprite, float width, float height) {
		this.pushStage(SetupTeardown.SPRITE);
		this.textureManager.bindTexture(sprite.textureId().to());
		MatrixGraphicsUtil.drawTexturedQuad(
				this.matrices.peek().getModel(),
				0,
				width,
				0,
				height,
				0,
				sprite.offsetX(),
				sprite.offsetX() + sprite.width(),
				sprite.offsetY(),
				sprite.offsetY() + sprite.height());
	}

	@Override
	public void drawSprite(Sprite.Sized sized) {
		this.drawSprite(sized.sprite, sized.width, sized.height);
	}

	@Override
	public void drawLine(float x1, float y1, float x2, float y2, int color) {
		this.pushStage(SetupTeardown.FILL);
		Matrix4f matrix = this.matrices.peek().getModel();
		int a = color >> 24 & 255;
		int r = color >> 16 & 255;
		int g = color >> 8 & 255;
		int b = color & 255;
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, x1, y1, 0).color(r, g, b, a).next();
		bufferBuilder.vertex(matrix, x2, y2, 0).color(r, g, b, a).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}

	@Override
	public void fillRect(float x, float y, float width, float height, int color) {
		this.pushStage(SetupTeardown.FILL);
		MatrixGraphicsUtil.fill(this.matrices.peek().getModel(), x, y, 0, x, y + height, 0, x + width, y + height, 0, x + width, y, 0, color);
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
		return this.pop;
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
	protected void pushStage(@Nullable SetupTeardown stage) {
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

	public ItemRenderer getItemRenderer() {
		ItemRenderer itemRenderer = this.itemRenderer;
		if (itemRenderer == null) {
			itemRenderer = this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
		}
		return itemRenderer;
	}

	@Override
	public MatrixStack getTransformationMatrix() {
		return this.matrices;
	}
}
