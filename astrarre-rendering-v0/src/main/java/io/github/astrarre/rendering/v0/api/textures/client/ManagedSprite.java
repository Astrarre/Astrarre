package io.github.astrarre.rendering.v0.api.textures.client;

import java.util.Objects;

import io.github.astrarre.rendering.v0.api.textures.Sprite;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * SpriteInfos can be animated textures and come from atlases
 * @see SpriteManager
 */
@Environment(EnvType.CLIENT)
public interface ManagedSprite extends Sprite {
	static ManagedSprite of(net.minecraft.client.texture.Sprite sprite) {
		return (ManagedSprite) sprite;
	}
	
	Sprite getTexture(int frame);

	/**
	 * @return the number of unique frames
	 */
	int frames();

	int width(int frame);

	int height(int frame);
}
