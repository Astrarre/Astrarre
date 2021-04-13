package io.github.astrarre.gui.v0.api.graphics;

import java.util.List;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.rendering.v0.api.Graphics2d;
import io.github.astrarre.rendering.v0.api.Graphics3d;
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
	static List<OrderedText> wrap(Text text, int width) {
		return MinecraftClient.getInstance().textRenderer.wrapLines(text, width);
	}

	void drawItem(ItemKey stack);
	void drawItem(ItemStack stack);
	void drawTooltip(List<Text> text);
	void drawOrderedTooltip(List<OrderedText> text);
	void drawTooltip(ItemStack stack);
	void drawTooltip(ItemStack stack, int maxWidth);
}
