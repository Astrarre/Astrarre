package io.github.astrarre.rendering.v0.api.textures;

import net.minecraft.client.texture.Sprite;

public interface SpriteInfo {
	static SpriteInfo of(Sprite sprite) {
		return (SpriteInfo) sprite;
	}

	class TexturePart {
		public final Texture texture;
		public final int offX, offY, width, height;

		public TexturePart(Texture texture, int offX, int offY, int width, int height) {
			this.texture = texture;
			this.offX = offX;
			this.offY = offY;
			this.width = width;
			this.height = height;
		}
	}

	TexturePart getTexture(int frame);

	int frames();
}
