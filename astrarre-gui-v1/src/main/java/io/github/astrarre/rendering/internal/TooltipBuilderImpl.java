package io.github.astrarre.rendering.internal;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.v1.api.component.icon.Icon;
import io.github.astrarre.gui.v1.api.component.icon.ScrollingLabelIcon;
import io.github.astrarre.gui.v1.edge.TooltipComponents;
import io.github.astrarre.rendering.internal.mixin.ScreenAccess;
import io.github.astrarre.rendering.v1.api.plane.TextRenderer;
import io.github.astrarre.rendering.v1.api.plane.TooltipBuilder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vector4f;

public class TooltipBuilderImpl implements TooltipBuilder {
	private static final LiteralText TEXT = new LiteralText("EMPTY");
	final List<TooltipComponent> components = new ArrayList<>();
	final Renderer2DImpl impl;

	public TooltipBuilderImpl(Renderer2DImpl impl) {
		this.impl = impl;
	}

	@Override
	public TooltipBuilder add(Icon icon) {
		this.components.add(TooltipComponents.from(icon));
		return this;
	}

	@Override
	public TextRenderer textRenderer(int color, boolean shadow) {
		return new TextRender(color, shadow);
	}

	@Override
	public void render() {
		this.impl.flush();
		MatrixStack stack = this.impl.stack;
		Vector4f origin = new Vector4f(0, 0, 0, 1);
		origin.transform(stack.peek().getModel());
		Screen screen = new Screen(TEXT) {};
		float x = origin.getX(), y = origin.getY();
		screen.init(MinecraftClient.getInstance(), this.currentWidth(), this.currentHeight()); // this is wrong too
		stack.push();
		stack.translate(-x, -y, 0);
		((ScreenAccess)screen).callRenderTooltipFromComponents(stack, this.components, (int) x, (int) y);
		stack.pop();
	}

	@Override
	public int currentWidth() {
		net.minecraft.client.font.TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
		return this.components.stream().mapToInt(t -> t.getWidth(renderer)).sum();
	}

	@Override
	public int currentHeight() {
		return this.components.stream().mapToInt(t -> t.getHeight()).sum();
	}

	final class TextRender implements TextRenderer {
		final TextRenderer renderer = TooltipBuilderImpl.this.impl.text(0, 0, 0, false);
		final int color;
		final boolean shadow;

		TextRender(int color, boolean shadow) {
			this.color = color;
			this.shadow = shadow;
		}

		@Override
		public int textHeight() {
			return this.renderer.textHeight();
		}

		@Override
		public int width(Text text) {
			return this.renderer.width(text);
		}

		@Override
		public int width(String text) {
			return this.renderer.width(text);
		}

		@Override
		public int width(OrderedText text) {
			return this.renderer.width(text);
		}

		@Override
		public List<OrderedText> wrap(Text text, int width) {
			return this.renderer.wrap(text, width);
		}

		@Override
		public void renderWrappedText(Text text, int width) {
			TooltipBuilderImpl.this.components.add(TooltipComponents.from(new WrappedLabelIcon(this, text, width, this.color, this.shadow)));
		}

		@Override
		public void renderScrollingText(Text text, float offsetX, float width, boolean loop) {
			var temp = new ScrollingLabelIcon(text, this.color, this.shadow, width, () -> (int) offsetX);
			TooltipBuilderImpl.this.components.add(TooltipComponents.from(temp));
		}

		@Override
		public void render(OrderedText text) {
			TooltipBuilderImpl.this.components.add(TooltipComponents.from(Icon.text(text, this.color, this.shadow)));
		}
	}
}
