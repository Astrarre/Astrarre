package io.github.astrarre.rendering.v0.fabric;

import io.github.astrarre.rendering.internal.util.MatrixGraphicsUtil;
import io.github.astrarre.rendering.internal.util.SetupTeardown;
import io.github.astrarre.rendering.v0.api.Graphics3d;

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
public class FabricGraphics3d extends FabricGraphics2d implements Graphics3d {
	public FabricGraphics3d(MatrixStack matrices) {
		super(matrices);
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
		bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, x1, y1, z1).color(r, g, b, a).next();
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
}
