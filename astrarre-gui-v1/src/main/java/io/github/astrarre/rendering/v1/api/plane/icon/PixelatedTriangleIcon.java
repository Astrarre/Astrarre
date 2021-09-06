package io.github.astrarre.rendering.v1.api.plane.icon;

import io.github.astrarre.rendering.v1.api.space.Render3d;

/**
 * draws a pixelated triangle pointing upwards
 */
public record PixelatedTriangleIcon(int color, float width, float height) implements Icon {
	@Override
	public void render(Render3d render) {
		float thick = this.width / (this.height * 2 + 1);
		for(int i = 0; i < this.height; i++) {
			float width = (i * 2 + 1) * thick;
			float offset = (this.width - width) / 2;
			render.fill().rect(this.color, offset, i, width, 1);
		}
	}
}
