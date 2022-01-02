package io.github.astrarre.rendering.v1.api.plane.icon;

import io.github.astrarre.rendering.v1.api.plane.Texture;
import io.github.astrarre.rendering.v1.api.space.Render3d;

/**
 * @param width how large len draw the texture
 */
public record TextureIcon(Texture texture, int color, float offX, float offY, float width, float height) implements Icon {
	public TextureIcon(Texture texture, float offX, float offY, float width, float height) {
		this(texture, 0, offX, offY, width, height);
	}

	public TextureIcon(Texture texture, float width, float height) {
		this(texture, 0, 0, 0, width, height);
	}

	public TextureIcon(Texture texture) {
		this(texture, 0, 0, 0, 16, 16);
	}

	@Override
	public void render(Render3d render) {
		render.texture(this.texture, this.offX, this.offY, this.width, this.height);
	}

	public record Repeating(TextureIcon icon, float repeatX, float repeatY) implements Icon {
		@Override
		public float width() {
			return icon.width() * repeatX;
		}

		@Override
		public float height() {
			return icon.height() * repeatY;
		}

		@Override
		public void render(Render3d render) {
			for(int x = 0; x < this.repeatX; x++) {
				float sizeX = Math.min(this.repeatX - x, 1);
				for(int y = 0; y < this.repeatY; y++) {
					float sizeY = Math.min(this.repeatY - y, 1);
					try(var ignore = render.translate(this.icon.width() * x, this.icon.height() * y)) {
						render.texture(icon.texture.crop(sizeX, sizeY), icon.color, icon.offX, icon.offY, icon.width * sizeX, icon.height * sizeY);
					}
				}
			}
		}
	}
}
