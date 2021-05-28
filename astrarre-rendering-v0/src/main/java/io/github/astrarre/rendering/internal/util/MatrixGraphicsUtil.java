package io.github.astrarre.rendering.internal.util;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
public class MatrixGraphicsUtil {
	/**
	 * @see DrawableHelper#fill(Matrix4f, int, int, int, int, int)
	 */
	public static void fill(Matrix4f matrix,
			float x1,
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
		float a = (float) (color >> 24 & 255) / 255.0F;
		float r = (float) (color >> 16 & 255) / 255.0F;
		float g = (float) (color >> 8 & 255) / 255.0F;
		float b = (float) (color & 255) / 255.0F;
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, x1, y1, z1).color(r, g, b, a).next();
		bufferBuilder.vertex(matrix, x2, y2, z2).color(r, g, b, a).next();
		bufferBuilder.vertex(matrix, x3, y3, z3).color(r, g, b, a).next();
		bufferBuilder.vertex(matrix, x4, y4, z4).color(r, g, b, a).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}

	/**
	 * @see DrawableHelper#fillGradient(MatrixStack, int, int, int, int, int, int)
	 */
	public static void fillGradient(MatrixStack matrices,
			float x1,
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
			int colorStart,
			int colorEnd) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		fillGradient(matrices.peek().getModel(), bufferBuilder, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, colorStart, colorEnd);
		tessellator.draw();
	}

	protected static void fillGradient(Matrix4f matrix,
			BufferBuilder bufferBuilder,
			float x1,
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
		bufferBuilder.vertex(matrix, x1, y1, z1).color(startR, startG, startB, startA).next();
		bufferBuilder.vertex(matrix, x2, y2, z2).color(startR, startG, startB, startA).next();
		bufferBuilder.vertex(matrix, x3, y3, z3).color(endR, endG, endB, endA).next();
		bufferBuilder.vertex(matrix, x4, y4, z4).color(endR, endG, endB, endA).next();
	}

	public static void drawTexturedQuad(Matrix4f matrices, float x0, float x1, float y0, float y1, float z, float u0, float u1, float v0, float v1) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrices, x0, y1, z).texture(u0, v1).next();
		bufferBuilder.vertex(matrices, x1, y1, z).texture(u1, v1).next();
		bufferBuilder.vertex(matrices, x1, y0, z).texture(u1, v0).next();
		bufferBuilder.vertex(matrices, x0, y0, z).texture(u0, v0).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}
}
