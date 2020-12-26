package io.github.astrarre.gui.v0.api.components;

import io.github.astrarre.gui.v0.api.Graphics2d;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * something to draw on a screen
 */
public abstract class Component {
	/**
	 * The scale is always 'just right' for any given height and width, but not for position.
	 * These bounds are not enforced in the render method, they are only used for collision in the HUD and passing events in Widget
	 */
	public float height, width;
	public DynamicLocation location;

	/**
	 * @param tickDelta the 'fraction' of the tick that this is being rendered in.
	 */
	@Environment (EnvType.CLIENT)
	public abstract void render(Graphics2d g2d, float tickDelta);

	public Point2f getLocation(float screenWidth, float screenHeight) {
		return this.location.getLocation(screenWidth, screenHeight);
	}
}
