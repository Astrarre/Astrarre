package io.github.astrarre.rendering.v0.api;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.rendering.v0.api.util.Close;

import net.minecraft.item.ItemStack;

/**
 * Graphics3d, confusingly this is also used for GUIs because they can have layers and things can overlap
 * for GUIs, the Z axis is normal to the screen (pointing out at you) so to "roll" you actually "yaw"
 */
public interface Graphics3d extends Graphics2d {
	void drawItem(ItemKey stack);

	void drawItem(ItemStack stack);

	void drawLine(float x1, float y1, float z1, float x2, float y2, float z2, int color);

	@Override
	default void drawLine(float x1, float y1, float x2, float y2, int color) {
		this.drawLine(x1, y1, 0, x2, y2, 0, color);
	}

	// todo drawPolygon

	@Override
	default void fillRect(float x, float y, float width, float height, int color) {
		float x2 = x + width, y2 = y + height;
		this.fillRect(x, y, 0, x, y2, 0, x2, y2, 0, x2, y, 0, color);
	}

	/**
	 * fill a rectangle in 3d space. The points must be coplanar and in counter clockwise order
	 */
	void fillRect(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int color);
}
