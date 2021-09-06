package io.github.astrarre.rendering.v1.api.plane;

import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.util.v0.api.Edge;

import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

/**
 * @see #text()
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
	default void renderWrappedText(Text text, int width, boolean shadow) {
		this.text(shadow).renderWrappedText(text, width);
	}

	/**
	 * @see TextRenderer#renderScrollingText(Text, float, float, boolean)
	 */
	@Edge
	default void renderScrollingText(Text text, float offsetX, float width, boolean loop, boolean shadow) {
		this.text(shadow).renderScrollingText(text, offsetX, width, loop);
	}

	@Edge
	default void text(OrderedText text, boolean shadow) {
		this.text(shadow).render(text);
	}

	/**
	 * Draws text on the screen. The text is 9 pixels tall
	 */
	@Edge
	default void text(Text text, boolean shadow) {
		this.text(text.asOrderedText(), shadow);
	}

	default void text(String text, boolean shadow) {
		this.text(new LiteralText(text), shadow);
	}

	/**
	 * Actually renders the tooltip, this must be called atleast once!
	 */
	void text();

	int currentWidth();

	int currentHeight();
}
