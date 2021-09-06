package io.github.astrarre.rendering.v1.api.plane.icon;

import io.github.astrarre.rendering.v1.api.plane.Texture;
import io.github.astrarre.rendering.v1.api.space.Render3d;

/**
 * @param width how large len draw the texture
 */
public record TextureIcon(Texture texture, float offX, float offY, float width, float height) implements Icon {
	public TextureIcon(Texture texture, float width, float height) {
		this(texture, 0, 0, width, height);
	}

	public TextureIcon(Texture texture) {
		this(texture, 0, 0, 16, 16);
	}

	@Override
	public void render(Render3d render) {
		render.texture(this.texture, this.offX, this.offY, this.width, this.height);
	}
}
