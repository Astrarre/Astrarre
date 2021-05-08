package io.github.astrarre.rendering.v0.api.textures.client;

import java.util.Objects;

import io.github.astrarre.rendering.v0.api.textures.Sprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


/**
 * SpriteInfos can be animated textures and come from atlases
 * @see SpriteManager
 */
@OnlyIn (Dist.CLIENT)
public interface ManagedSprite extends Sprite {
	static ManagedSprite of(net.minecraft.client.texture.Sprite sprite) {
		return (ManagedSprite) sprite;
	}
	
	Sprite getTexture(int frame);
	int frames();
	int width(int frame);
	int height(int frame);


}
