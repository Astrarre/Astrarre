package io.github.astrarre.rendering.v0.api.textures;

import net.minecraft.client.texture.Sprite;

public interface SpriteInfo {
	static SpriteInfo of(Sprite sprite) {
		return (SpriteInfo) sprite;
	}

	TexturePart getTexture(int frame);

	int frames();
}
