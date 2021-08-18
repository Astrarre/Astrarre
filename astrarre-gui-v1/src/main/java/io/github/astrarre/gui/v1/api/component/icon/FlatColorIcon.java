package io.github.astrarre.gui.v1.api.component.icon;

import io.github.astrarre.gui.v1.api.cursor.Cursor;
import io.github.astrarre.rendering.v1.api.space.Render3d;

/**
 * @param width how large to draw the texture
 */
public record FlatColorIcon(int argb, float offX, float offY, float width, float height) implements Icon {
	public FlatColorIcon(int color, float width, float height) {
		this(color, 0, 0, width, height);
	}

	public FlatColorIcon(int color) {
		this(color, 0, 0, 16, 16);
	}

	@Override
	public void render(Render3d render) {
		render.fill().rect(this.argb, this.offX, this.offY, this.width, this.height);
	}
}
