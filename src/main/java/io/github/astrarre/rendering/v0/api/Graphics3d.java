package io.github.astrarre.rendering.v0.api;

import io.github.astrarre.rendering.v0.fabric.FabricGraphics;
import io.github.astrarre.v0.client.texture.Sprite;

public interface Graphics3d extends FabricGraphics {
	/**
	 * draws a sprite along the xy plane
	 */
	void drawSprite(Sprite sprite);
}
