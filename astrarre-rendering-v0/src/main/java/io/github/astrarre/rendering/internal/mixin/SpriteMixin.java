package io.github.astrarre.rendering.internal.mixin;

import io.github.astrarre.rendering.v0.api.textures.SpriteInfo;
import io.github.astrarre.rendering.v0.api.textures.Texture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

@Mixin(Sprite.class)
public abstract class SpriteMixin implements SpriteInfo {

	@Shadow @Final private SpriteAtlasTexture atlas;

	@Shadow @Final private int[] frameXs;

	@Shadow @Final private int[] frameYs;

	@Shadow @Final protected NativeImage[] images;

	@Override
	public Texture getTexture(int frame) {
		int x = this.frameXs[frame], y = this.frameYs[frame];
		NativeImage image = this.images[frame];
		return new Texture(this.atlas.getId(), x, y, image.getWidth(), image.getHeight());
	}

	@Override
	public int frames() {
		return this.frameXs.length;
	}
}
