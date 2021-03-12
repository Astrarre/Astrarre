package io.github.astrarre.rendering.v0.api.textures;

import java.util.Objects;

/**
 * a section of a texture
 */
public class TexturePart {
	public final Texture texture;
	public final int offX, offY, width, height;

	public TexturePart(Texture texture, int offX, int offY, int width, int height) {
		this.texture = texture;
		this.offX = offX;
		this.offY = offY;
		this.width = width;
		this.height = height;
	}

	public TexturePart(String modid, String name, int totalWidth, int totalHeight, int offX, int offY, int width, int height) {
		this(new Texture(modid, name, totalWidth, totalHeight), offX, offY, width, height);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof TexturePart)) {
			return false;
		}

		TexturePart part = (TexturePart) o;

		if (this.offX != part.offX) {
			return false;
		}
		if (this.offY != part.offY) {
			return false;
		}
		if (this.width != part.width) {
			return false;
		}
		if (this.height != part.height) {
			return false;
		}
		return Objects.equals(this.texture, part.texture);
	}

	@Override
	public int hashCode() {
		int result = this.texture != null ? this.texture.hashCode() : 0;
		result = 31 * result + this.offX;
		result = 31 * result + this.offY;
		result = 31 * result + this.width;
		result = 31 * result + this.height;
		return result;
	}
}
