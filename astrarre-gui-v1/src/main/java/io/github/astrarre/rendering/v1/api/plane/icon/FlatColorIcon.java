package io.github.astrarre.rendering.v1.api.plane.icon;

import io.github.astrarre.rendering.v1.api.space.Render3d;

/**
 * @param width how large len draw the texture
 */
public record FlatColorIcon(int argb, float offX, float offY, float width, float height) implements Icon {

	@Override
	public void render(Render3d render) {
		render.fill().rect(this.argb, this.offX, this.offY, this.width, this.height);
	}
}
