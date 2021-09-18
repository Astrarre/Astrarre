package io.github.astrarre.rendering.v1.api.plane;

import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.util.v0.api.Edge;

import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

/**
 * @see #render()
 */
public interface TooltipBuilder {
	TooltipBuilder add(Icon icon);

	default TextRenderer text(boolean shadow) {
		return this.text(0xffffffff, shadow);
	}

	/**
	 * Each render call to this text renderer will add another component to the tooltip builder
	 */
	TextRenderer text(int color, boolean shadow);

	@Edge
	default TooltipBuilder renderWrappedText(Text text, int width, boolean shadow) {
		this.text(shadow).renderWrappedText(text, width);
		return this;
	}

	@Edge
	default TooltipBuilder scrollingText(Text text, float msPerPixel, float width, boolean loop, boolean shadow) {
		float textOffset = ((System.currentTimeMillis() % 60_000L) / msPerPixel) % this.text(true).width(text);
		return this.renderScrollingText(text, textOffset, width, loop, shadow);
	}

	/**
	 * @see TextRenderer#renderScrollingText(Text, float, float, boolean)
	 */
	@Edge
	default TooltipBuilder renderScrollingText(Text text, float offsetX, float width, boolean loop, boolean shadow) {
		this.text(shadow).renderScrollingText(text, offsetX, width, loop);
		return this;
	}

	@Edge
	default TooltipBuilder text(OrderedText text, boolean shadow) {
		this.text(shadow).render(text);
		return this;
	}

	/**
	 * Draws text on the screen. The text is 9 pixels tall
	 */
	@Edge
	default TooltipBuilder text(Text text, boolean shadow) {
		this.text(text.asOrderedText(), shadow);
		return this;
	}

	default TooltipBuilder text(String text, boolean shadow) {
		this.text(new LiteralText(text), shadow);
		return this;
	}

	/**
	 * Actually renders the tooltip, this must be called atleast once!
	 */
	void render();

	int currentWidth();

	int currentHeight();
}
