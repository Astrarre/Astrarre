package io.github.astrarre.rendering.internal;

import io.github.astrarre.rendering.internal.access.BufferBuilderAccess;
import io.github.astrarre.rendering.internal.ogl.VertexRendererImpl;
import io.github.astrarre.rendering.v1.api.space.item.ItemRenderer;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import io.github.astrarre.rendering.v1.api.space.item.ModelTransformType;
import io.github.astrarre.rendering.v1.api.util.AngleFormat;
import io.github.astrarre.rendering.v1.edge.VertexRenderer;
import io.github.astrarre.util.v0.api.SafeCloseable;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

public class Renderer3DImpl extends Renderer2DImpl implements Render3d, ItemRenderer {
	final net.minecraft.client.render.item.ItemRenderer itemRenderer;
	final VertexRendererImpl renderer;


	public Renderer3DImpl(TextRenderer renderer,
			MatrixStack stack,
			BufferBuilder consumer,
			net.minecraft.client.render.item.ItemRenderer itemRenderer) {
		this(renderer, stack, consumer, itemRenderer, ((BufferBuilderAccess)consumer).getRenderer());
	}

	protected Renderer3DImpl(TextRenderer renderer,
			MatrixStack stack,
			BufferBuilder consumer,
			net.minecraft.client.render.item.ItemRenderer itemRenderer,
			VertexRendererImpl glRenderer) {
		super(renderer, stack, consumer);
		this.itemRenderer = itemRenderer;
		this.renderer = glRenderer;
		glRenderer.stack = stack;
	}

	@Override
	public ItemRenderer item() {
		return this;
	}

	@Override
	public void render(ModelTransformType type,
			@Nullable LivingEntity entity,
			@Nullable World world,
			ItemStack stack,
			int light,
			int overlay,
			long seed) {
		boolean leftHanded = type instanceof ModelTransformType.Holding h && h.hand == ModelTransformType.Hand.LEFT;
		this.itemRenderer.renderItem(entity, stack, type.getMode(), leftHanded, this.stack, VertexConsumerProvider.immediate(this.buffer), world, light, overlay, (int)seed);
	}

	@Override
	public VertexRenderer edge() {
		return this.renderer;
	}

	@Override
	public SafeCloseable transform(Transform3d transform) {
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
	public SafeCloseable rotate(float axisX, float axisY, float axisZ, AngleFormat format, float theta) {
		theta = format.convert(AngleFormat.RADIAN, theta);
		float f = (float) Math.sin(theta / 2.0F);
		float x = axisX * f;
		float y = axisY * f;
		float z = axisZ * f;
		float w = (float) Math.cos(theta / 2.0F);
		this.stack.push();
		MatrixStack.Entry entry = this.stack.peek();
		multiply(entry.getModel(), entry.getNormal(), x, y, z, w);
		return this.pop;
	}

	@Override
	public SafeCloseable rotate(Direction.Axis axis, AngleFormat format, float theta) {
		float x = 0;
		float y = 0;
		float z = 0;
		switch(axis) {
			case X -> x = 1;
			case Y -> y = 1;
			case Z -> z = 1;
		}
		return this.rotate(x, y, z, format, theta);
	}

	@Override
	public void line(int color, float x1, float y1, float z1, float x2, float y2, float z2) {

		this.push(SetupImpl.LINE);
		int r = color & 0xFF, g = (color >> 8) & 0xFF, b = (color >> 16) & 0xFF, a = (color >> 24) & 0xFF;
		Matrix4f matrix = this.stack.peek().getModel();

		this.buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).next();
		this.buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).next();
	}

	@Override
	public void flush() {
		this.edge().flush();
		super.flush();
	}

}
