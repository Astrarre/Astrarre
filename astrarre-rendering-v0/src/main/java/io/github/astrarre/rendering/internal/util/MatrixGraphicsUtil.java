package io.github.astrarre.rendering.internal.util;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
public class MatrixGraphicsUtil {


	public static void line(MatrixStack matrices, float x0, float x1, float y0, float y1, int color) {
		Matrix4f matrix = matrices.peek().getModel();

		float a = (float)(color >> 24 & 255) / 255.0F;
		float r = (float)(color >> 16 & 255) / 255.0F;
		float g = (float)(color >> 8 & 255) / 255.0F;
		float b = (float)(color & 255) / 255.0F;
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();

		bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, x0, y0, 0F).color(r, g, b, a).next();
		bufferBuilder.vertex(matrix, x1, y1, 0F).color(r, g, b, a).next();
		bufferBuilder.end();

		BufferRenderer.draw(bufferBuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	/**
	 * @see DrawableHelper#fill(Matrix4f, int, int, int, int, int)
	 */
	public static void fill(Matrix4f matrix, float x1, float y1, float x2, float y2, int color) {
		// least to greatest
		if (x1 < x2) {
			float temp = x1;
			x1 = x2;
			x2 = temp;
		}

		if (y1 < y2) {
			float temp = y1;
			y1 = y2;
			y2 = temp;
		}

		float a = (float) (color >> 24 & 255) / 255.0F;
		float r = (float) (color >> 16 & 255) / 255.0F;
		float g = (float) (color >> 8 & 255) / 255.0F;
		float b = (float) (color & 255) / 255.0F;
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, x1, y2, 0.0F).color(r, g, b, a).next();
		bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(r, g, b, a).next();
		bufferBuilder.vertex(matrix, x2, y1, 0.0F).color(r, g, b, a).next();
		bufferBuilder.vertex(matrix, x1, y1, 0.0F).color(r, g, b, a).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	/**
	 * @see DrawableHelper#fillGradient(MatrixStack, int, int, int, int, int, int)
	 */
	public static void fillGradient(MatrixStack matrices,
			float xStart,
			float yStart,
			float xEnd,
			float yEnd,
			float zOffset,
			int colorStart,
			int colorEnd) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		//RenderSystem.disableAlphaTest();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		fillGradient(matrices.peek().getModel(), bufferBuilder, xStart, yStart, xEnd, yEnd, zOffset, colorStart, colorEnd);
		tessellator.draw();
		RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		//RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}

	protected static void fillGradient(Matrix4f matrix,
			BufferBuilder bufferBuilder,
			float xStart,
			float yStart,
			float xEnd,
			float yEnd,
			float z,
			int colorStart,
			int colorEnd) {
		float startA = (float) (colorStart >> 24 & 255) / 255.0F;
		float startR = (float) (colorStart >> 16 & 255) / 255.0F;
		float startG = (float) (colorStart >> 8 & 255) / 255.0F;
		float startB = (float) (colorStart & 255) / 255.0F;
		float endA = (float) (colorEnd >> 24 & 255) / 255.0F;
		float endR = (float) (colorEnd >> 16 & 255) / 255.0F;
		float endG = (float) (colorEnd >> 8 & 255) / 255.0F;
		float endB = (float) (colorEnd & 255) / 255.0F;
		bufferBuilder.vertex(matrix, xEnd, yStart, z).color(startR, startG, startB, startA).next();
		bufferBuilder.vertex(matrix, xStart, yStart, z).color(startR, startG, startB, startA).next();
		bufferBuilder.vertex(matrix, xStart, yEnd, z).color(endR, endG, endB, endA).next();
		bufferBuilder.vertex(matrix, xEnd, yEnd, z).color(endR, endG, endB, endA).next();
	}
}
