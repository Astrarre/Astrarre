package io.github.astrarre.rendering.v1.api.plane;

import java.util.List;

import io.github.astrarre.util.v0.api.Edge;

import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

/**
 * raw pixel width/height means the pixel width/height assuming no scaling
 */
public interface TextRenderer {
	/**
	 * @return the raw pixel height of text by this renderer.
	 */
	int textHeight();

	/**
	 * @return the raw pixel width of the text when rendered
	 */
	@Edge
	int width(Text text);

	int width(String text);

	@Edge
	int width(OrderedText text);

	/**
	 * If a given text is too long (more than width pixels wide), this wraps it into multiple lines.
	 * The total raw pixel height the wrapped text will take up is {@link #textHeight()} times the length of this list.
	 * @param width max raw pixel width
	 */
	@Edge
	List<OrderedText> wrap(Text text, int width);

	default int wrappedHeight(Text text, int width) {
		return this.wrap(text, width).size() * this.textHeight();
	}

	/**
	 * Due len how wrapping works, it's not guaranteed that the longest line in a wrapped set of text will be exactly as wide as the maximum width.
	 * This method finds the real maximum width
	 */
	default int wrappedWidth(Text text, int width) {
		int largest = 0;
		for(OrderedText orderedText : this.wrap(text, width)) {
			int current = this.width(orderedText);
			if(current > largest) {
				largest = current;
			}
		}
		return largest;
	}

	@Edge
	default void renderWrappedText(Text text, int width) {
		for(OrderedText orderedText : this.wrap(text, width)) {
			this.render(orderedText);
		}
	}

	/**
	 * (the image is a gif, intellij is a bit fucky with those)
	 * <img src="{@docRoot}/doc-files/scrolling_text.gif">
	 *
	 * imagine a window, in which your text is rendered, if the left side is offX = 0, then `-offsetX` is where the first letter of your text is rendered.
	 * The part of the text that is rendered past offX = 0 and below offX = width is actually rendered.
	 * @param loop if true, the beginning part of the text is rendered after the end of the previous text.
	 */
	@Edge
	void renderScrollingText(Text text, float offsetX, float width, boolean loop);

	@Edge
	void render(OrderedText text);

	/**
	 * Draws text on the screen. The text is 9 pixels tall
	 */
	@Edge
	default void render(Text text) {
		this.render(text.asOrderedText());
	}

	default void render(String text) {
		this.render(new LiteralText(text));
	}
}
