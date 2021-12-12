package io.github.astrarre.rendering.internal;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.rendering.v1.api.plane.Render2d;
import io.github.astrarre.rendering.v1.api.plane.ShapeRenderer;
import io.github.astrarre.rendering.v1.api.plane.TextRenderer;
import io.github.astrarre.rendering.v1.api.plane.Texture;
import io.github.astrarre.rendering.v1.api.plane.TooltipBuilder;
import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.util.AngleFormat;
import io.github.astrarre.rendering.v1.edge.Stencil;
import io.github.astrarre.util.v0.api.SafeCloseable;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class Renderer2DImpl implements Render2d {
	public static final Stencil STENCIL = Stencil.newInstance();

	final net.minecraft.client.font.TextRenderer textRenderer;
	final int width, height;
	public final MatrixStack stack;
	final BufferBuilder buffer;
	final SafeCloseable pop;
	final ShapeRenderer outline = new ShapeRendererImpl(SetupImpl.OUTLINE, SetupImpl.OUTLINE, true);
	final ShapeRenderer fill = new ShapeRendererImpl(SetupImpl.QUAD, SetupImpl.TRIANGLE, false);
	Setup active;

	public Renderer2DImpl(net.minecraft.client.font.TextRenderer renderer, int width, int height, MatrixStack stack, BufferBuilder consumer) {
		this.textRenderer = renderer;
		this.width = width;
		this.height = height;
		this.stack = stack;
		this.pop = stack::pop;
		this.buffer = consumer;
	}

	@Override
	public SafeCloseable transform(Transform2d transform) {
		MatrixStack old = this.stack;
		MatrixTransform3D t = Validate.instanceOf(transform, MatrixTransform3D.class, "Custom Transform2Ds not yet supported!");
		old.push();
		old.multiplyPositionMatrix(t.matrix());
		return this.pop;
	}

	@Override
	public SafeCloseable translate(float offX, float offY) {
		MatrixStack old = this.stack;
		old.push();
		old.translate(offX, offY, 0);
		return this.pop;
	}

	@Override
	public SafeCloseable scale(float scaleX, float scaleY) {
		MatrixStack old = this.stack;
		old.push();
		old.scale(scaleX, scaleY, 0);
		return this.pop;
	}

	@Override
	public SafeCloseable rotate(AngleFormat format, float theta) {
		theta = format.convert(AngleFormat.RADIAN, theta);

		MatrixStack old = this.stack;
		old.push();

		float z = (float) Math.sin(theta / 2.0F);
		float w = (float) Math.cos(theta / 2.0F);

		Matrix4f model = old.peek().getPositionMatrix();
		Matrix3f normal = old.peek().getNormalMatrix();
		float l = 2.0F * z * z;
		normal.a00 = model.a00 = 1.0F - l;
		normal.a11 = model.a11 = 1.0F - l;
		normal.a22 = model.a22 = 1.0F;
		model.a33 = 1.0F;
		float r = z * w;
		normal.a10 = model.a10 = 2.0F * (r);
		normal.a01 = model.a01 = 2.0F * (-r);

		return this.pop;
	}

	@Override
	public ShapeRenderer fill() {
		return this.fill;
	}

	@Override
	public ShapeRenderer outline() {
		return this.outline;
	}

	@Override
	public TextRenderer text(int color, float x, float y, boolean shadow) {
		return new TextRendererImpl(this.textRenderer, color, x, y, shadow);
	}

	@Override
	public TooltipBuilder tooltip() {
		return new TooltipBuilderImpl(this);
	}

	@Override
	public void line(int color, float x1, float y1, float x2, float y2) {
		this.push(SetupImpl.LINE);
		int b = color & 0xFF, g = (color >> 8) & 0xFF, r = (color >> 16) & 0xFF, a = (color >> 24) & 0xFF;
		Matrix4f matrix = this.stack.peek().getPositionMatrix();
		this.buffer.vertex(matrix, x1, y1, 1).color(r, g, b, a).next();
		this.buffer.vertex(matrix, x2, y2, 1).color(r, g, b, a).next();
	}

	@Override
	public void texture(Texture texture, float offX, float offY, float width, float height) {
		this.push(SetupImpl.TEXTURE);
		RenderSystem.setShaderTexture(0, texture.texture().to());
		Matrix4f matrix = this.stack.peek().getPositionMatrix();
		float x2 = offX + width, y2 = offY + height;
		float vx = texture.offX() + texture.width(), vy = texture.offY() + texture.height();
		this.buffer.vertex(matrix, offX, offY, 1).texture(texture.offX(), texture.offY()).next();
		this.buffer.vertex(matrix, offX, y2, 1).texture(texture.offX(), vy).next();
		this.buffer.vertex(matrix, x2, y2, 1).texture(vx, vy).next();
		this.buffer.vertex(matrix, x2, offY, 1).texture(vx, texture.offY()).next();
	}

	@Override
	public void flush() {
		this.push(null);
	}

	static void multiply(Matrix4f model, Matrix3f normal, float x, float y, float z, float w) {
		float j = 2.0F * x * x;
		float k = 2.0F * y * y;
		float l = 2.0F * z * z;
		model.a00 = normal.a00 = 1.0F - k - l;
		model.a11 = normal.a11 = 1.0F - l - j;
		model.a22 = normal.a22 = 1.0F - j - k;

		float m = x * y;
		float n = y * z;
		float o = z * x;
		float p = x * w;
		float q = y * w;
		float r = z * w;
		model.a10 = normal.a10 = 2.0F * (m + r);
		model.a01 = normal.a01 = 2.0F * (m - r);
		model.a20 = normal.a20 = 2.0F * (o - q);
		model.a02 = normal.a02 = 2.0F * (o + q);
		model.a21 = normal.a21 = 2.0F * (n + p);
		model.a12 = normal.a12 = 2.0F * (n - p);

		model.a33 = 1.0F;
	}

	final void push(Setup setup) {
		Setup active = this.active;
		BufferBuilder builder = this.buffer;
		if(active != null) {
			active.takedown(builder);
		}

		this.active = setup;
		if(setup != null) {
			setup.setup(builder);
		}
	}

	interface Setup {
		void setup(BufferBuilder builder);

		void takedown(BufferBuilder builder);
	}

	class ShapeRendererImpl implements ShapeRenderer {
		final SetupImpl rect, tri;
		final boolean close;

		ShapeRendererImpl(SetupImpl rect, SetupImpl tri, boolean close) {
			this.rect = rect;
			this.tri = tri;
			this.close = close;
		}

		@Override
		public void rect(int color, float offX, float offY, float width, float height) {
			Renderer2DImpl.this.push(this.rect);
			int b = color & 0xFF, g = (color >> 8) & 0xFF, r = (color >> 16) & 0xFF, a = (color >> 24) & 0xFF;
			Matrix4f matrix = Renderer2DImpl.this.stack.peek().getPositionMatrix();
			float x2 = offX + width, y2 = offY + height;
			Renderer2DImpl.this.buffer.vertex(matrix, offX, offY, 0).color(r, g, b, a).next();
			Renderer2DImpl.this.buffer.vertex(matrix, offX, y2, 0).color(r, g, b, a).next();
			Renderer2DImpl.this.buffer.vertex(matrix, x2, y2, 0).color(r, g, b, a).next();
			Renderer2DImpl.this.buffer.vertex(matrix, x2, offY, 0).color(r, g, b, a).next();
			if(this.close) {
				Renderer2DImpl.this.buffer.vertex(matrix, offX, offY, 0).color(r, g, b, a).next();
				Renderer2DImpl.this.push(null);
			}
		}

		/*@Override
		public void triangle(int color, float x1, float y1, float x2, float y2, float x3, float y3) {
			Renderer2DImpl.this.push(this.tri);
			int r = color & 0xFF, g = (color >> 8) & 0xFF, b = (color >> 16) & 0xFF, a = (color >> 24) & 0xFF;
			Matrix4f matrix = Renderer2DImpl.this.stack.peek().getModel();
			Renderer2DImpl.this.buffer.vertex(matrix, x1, y1, 0).color(r, g, b, a).next();
			Renderer2DImpl.this.buffer.vertex(matrix, x2, y2, 0).color(r, g, b, a).next();
			Renderer2DImpl.this.buffer.vertex(matrix, x3, y3, 0).color(r, g, b, a).next();
			if(this.close) {
				Renderer2DImpl.this.buffer.vertex(matrix, x1, y1, 0).color(r, g, b, a).next();
				Renderer2DImpl.this.push(null);
			}
		}*/
	}

	class TextRendererImpl implements TextRenderer {
		final net.minecraft.client.font.TextRenderer renderer;
		final int color;
		final float x;
		final float y;
		final boolean shadow;

		TextRendererImpl(net.minecraft.client.font.TextRenderer renderer, int color, float x, float y, boolean shadow) {
			this.renderer = renderer;
			this.color = color;
			this.x = x;
			this.y = y;
			this.shadow = shadow;
		}

		@Override
		public int textHeight() {
			return this.renderer.fontHeight;
		}

		@Override
		public int width(Text text) {
			return this.renderer.getWidth(text);
		}

		@Override
		public int width(String text) {
			return this.renderer.getWidth(text);
		}

		@Override
		public int width(OrderedText text) {
			return this.renderer.getWidth(text);
		}

		@Override
		public List<OrderedText> wrap(Text text, int width) {
			return this.renderer.wrapLines(text, width);
		}

		@Override
		public void renderScrollingText(Text text, float offsetX, float width, boolean loop) {
			int textWidth = this.width(text);
			offsetX %= textWidth;

			int id = STENCIL.startStencil(Stencil.Type.TRACING);

			Renderer2DImpl.this.fill().rect(0xFFFFFFFF, this.x, this.y, width, this.textHeight());
			Renderer2DImpl.this.flush();

			STENCIL.fill(id);

			float start = this.x - offsetX;
			Matrix4f matrix = Renderer2DImpl.this.stack.peek().getPositionMatrix();
			while(start < (this.x + width)) {
				this.draw(text.asOrderedText(), start, this.y, this.color, matrix, this.shadow);
				start += textWidth + 3; // we use the computed textWidth instead cus otherwise it jitters
			}

			Renderer2DImpl.this.flush();

			STENCIL.endStencil(id);
			RenderSystem.enableDepthTest();
		}

		@Override
		public void render(Text text) {
			this.render(text.asOrderedText());
		}

		@Override
		public void render(OrderedText text) {
			Matrix4f matrix = Renderer2DImpl.this.stack.peek().getPositionMatrix();
			this.draw(text, this.x, this.y, this.color, matrix, this.shadow);
		}

		@Override
		public void render(String text) {
			Matrix4f matrix = Renderer2DImpl.this.stack.peek().getPositionMatrix();
			this.draw(text, this.x, this.y, this.color, matrix, this.shadow, this.renderer.isRightToLeft());
		}

		private int draw(OrderedText text, float x, float y, int color, Matrix4f matrix, boolean shadow) {
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Renderer2DImpl.this.buffer);
			int i = this.renderer.draw(text, x, y, color, shadow, matrix, immediate, false, 0, 15728880);
			immediate.draw();
			RenderSystem.enableDepthTest();
			return i;
		}

		private int draw(String text, float x, float y, int color, Matrix4f matrix, boolean shadow, boolean mirror) {
			if (text == null) {
				return 0;
			} else {
				VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Renderer2DImpl.this.buffer);
				int i = this.renderer.draw(text, x, y, color, shadow, matrix, immediate, false, 0, 15728880, mirror);
				immediate.draw();
				RenderSystem.enableDepthTest();
				return i;
			}
		}
	}
}
