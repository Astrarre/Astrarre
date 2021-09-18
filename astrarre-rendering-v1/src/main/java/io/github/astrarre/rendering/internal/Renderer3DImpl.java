package io.github.astrarre.rendering.internal;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import io.github.astrarre.rendering.v1.api.space.item.ItemRenderer;
import io.github.astrarre.rendering.v1.api.space.item.ModelTransformType;
import io.github.astrarre.rendering.v1.api.util.AngleFormat;
import io.github.astrarre.util.v0.api.SafeCloseable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

public class Renderer3DImpl extends Renderer2DImpl implements Render3d, ItemRenderer {
	final net.minecraft.client.render.item.ItemRenderer itemRenderer;

	public Renderer3DImpl(TextRenderer renderer,
			MatrixStack stack,
			BufferBuilder consumer,
			net.minecraft.client.render.item.ItemRenderer itemRenderer,
			int width, int height) {
		super(renderer, width, height, stack, consumer);
		this.itemRenderer = itemRenderer;
	}

	@Override
	public ItemRenderer item() {
		return this;
	}

	static class Warn {
		static final Logger LOGGER = LogManager.getLogger(Renderer3DImpl.class);
		static {
			LOGGER.warn("Support for ModelTransformTypes other than GUI are flimsy at best!");
		}

		static void init() {}
	}

	@Override
	public void render(ModelTransformType type,
			@Nullable LivingEntity entity,
			@Nullable World world,
			ItemStack stack,
			int light,
			int overlay,
			long seed) {
		this.push(SetupImpl.ITEM);
		if(type == ModelTransformType.Standard.GUI) {
			// this is necessary, or something, tm
			MatrixStack s = RenderSystem.getModelViewStack();
			s.push();
			s.method_34425(this.stack.peek().getModel().copy());
			this.itemRenderer.renderInGuiWithOverrides(stack, 0, 0);
			this.itemRenderer.renderGuiItemOverlay(this.textRenderer, stack, 0, 0);
			RenderSystem.enableDepthTest();
			s.pop();
			RenderSystem.applyModelViewMatrix();
		} else {
			Warn.init();
			boolean leftHanded = type instanceof ModelTransformType.Holding h && h.hand == ModelTransformType.Hand.LEFT;
			var immediate = VertexConsumerProvider.immediate(this.buffer);
			this.itemRenderer.renderItem(entity, stack, type.getMode(), leftHanded, this.stack, immediate, world, light, overlay, (int) seed);
			immediate.draw();
		}
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
	public void line(int color, float x1, float y1, float z1, float x2, float y2, float z2) {
		this.push(SetupImpl.LINE);
		int r = color & 0xFF, g = (color >> 8) & 0xFF, b = (color >> 16) & 0xFF, a = (color >> 24) & 0xFF;
		Matrix4f matrix = this.stack.peek().getModel();

		this.buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).next();
		this.buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).next();
	}

	@Override
	public void flush() {
		super.flush();
	}

}
