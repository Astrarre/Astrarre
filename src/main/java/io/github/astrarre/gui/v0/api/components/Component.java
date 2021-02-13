package io.github.astrarre.gui.v0.api.components;

import io.github.astrarre.gui.v0.api.Graphics3d;
import io.github.astrarre.gui.v0.api.util.Rect4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * something to draw on a screen
 */
public abstract class Component {
	public DynamicBound bounds = DynamicBound.cartesian(-8, -8, 16, 16);

	/**
	 * @param tickDelta the 'fraction' of the tick that this is being rendered in.
	 */
	@Environment (EnvType.CLIENT)
	public abstract void render(Graphics3d g2d, float tickDelta);

	public final Rect4f getBounds(float screenWidth, float screenHeight) {
		return this.bounds.getLocation(screenWidth, screenHeight);
	}

	public boolean isIn(float screenWidth, float screenHeight, float x, float y) {
		Rect4f rect4F = this.getBounds(screenWidth, screenHeight);
		return x < (rect4F.x + rect4F.width) && x >= rect4F.x && y < (rect4F.y + rect4F.height) && y >= rect4F.y;
	}
}
