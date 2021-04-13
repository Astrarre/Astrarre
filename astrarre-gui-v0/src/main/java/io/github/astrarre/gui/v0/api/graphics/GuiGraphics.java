package io.github.astrarre.gui.v0.api.graphics;

import java.util.List;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.rendering.v0.api.Graphics2d;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Close;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

/**
 * Graphics3d, confusingly this is also used for GUIs because they can have layers and things can overlap
 * for GUIs, the Z axis is normal to the screen (pointing out at you) so to "roll" you actually "yaw"
 */
public interface GuiGraphics extends Graphics3d {
	Transformation HIGHLIGHT_TRANSLATION = Transformation.translate(0, 0, 300);
	static List<OrderedText> wrap(Text text, int width) {
		return MinecraftClient.getInstance().textRenderer.wrapLines(text, width);
	}

	/**
	 * draws an item with count 0 onto the screen, the resulting image is roughly 16x16
	 */
	void drawItem(ItemKey stack);
	/**
	 * draws an item with the given count onto the screen, the resulting image is roughly 16x16. It is slightly elevated in Z level
	 */
	void drawItem(ItemStack stack);

	/**
	 * tooltips are elevated
	 */
	void drawTooltip(List<Text> text);
	void drawOrderedTooltip(List<OrderedText> text);
	void drawTooltip(ItemStack stack);
	void drawTooltip(ItemStack stack, int maxWidth);

	/**
	 * does it's best to wrap the tooltip of this itemstack within the bounds of the screen
	 */
	void drawTooltipAutowrap(ItemStack stack);

	/**
	 * draws a highlight over the given area (like slots do when you hover over them)
	 */
	default void highlightRectangle(int width, int height) {
		try (Close close = this.applyTransformation(HIGHLIGHT_TRANSLATION)) {
			this.fillRect(width, height, 0x80ffffff);
		}
	}
}
