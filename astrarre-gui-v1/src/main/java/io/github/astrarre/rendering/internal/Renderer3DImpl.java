package io.github.astrarre.rendering.internal;

import io.github.astrarre.rendering.v1.api.space.Render3D;
import io.github.astrarre.rendering.v1.api.space.Transform3D;
import io.github.astrarre.rendering.v1.api.util.AngleFormat;
import io.github.astrarre.util.v0.api.SafeCloseable;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

public class Renderer3DImpl extends Renderer2DImpl implements Render3D {
	public Renderer3DImpl(MatrixStack stack, BufferBuilder consumer) {
		super(stack, consumer);
	}

	@Override
	public SafeCloseable transform(Transform3D transform) {
		return super.transform(transform);
	}

	@Override
	public SafeCloseable translate(float offX, float offY, float offZ) {
		MatrixStack old = this.stack;
		old.push();
		old.translate(offX, offY, offZ);
		return this.pop;
	}

	@Override
	public SafeCloseable scale(float scaleX, float scaleY, float scaleZ) {
		MatrixStack old = this.stack;
		old.push();
		old.scale(scaleX, scaleY, scaleZ);
		return this.pop;
	}

	// we can in theory used sine squared instead of angles, the matrix stuff requires squared angles anyways

	@Override
	public SafeCloseable rotate(Direction.Axis axis, AngleFormat format, float theta) {
		theta = format.convert(AngleFormat.RADIAN, theta);
		float f = (float) Math.sin(theta / 2.0F);
		float x = 0;
		float y = 0;
		float z = 0;
		switch(axis) {
			case X -> x = f;
			case Y -> y = f;
			case Z -> z = f;
		}
		float w = (float) Math.cos(theta / 2.0F);
		this.stack.push();
		MatrixStack.Entry entry = this.stack.peek();
		multiply(entry.getModel(), entry.getNormal(), x, y, z, w);
		return this.pop;
	}

	@Override
	public void line(int color, float x1, float y1, float z1, float x2, float y2, float z2) {
		this.push(SetupImpl.LINE);
		int r = color & 0xFF, g = (color >> 8) & 0xFF, b = (color >> 16) & 0xFF, a = (color >> 24) & 0xFF;
		Matrix4f matrix = this.stack.peek().getModel();
		this.buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).next();
		this.buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).next();
	}
}
