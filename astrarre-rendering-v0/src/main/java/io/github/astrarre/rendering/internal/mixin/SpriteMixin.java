package io.github.astrarre.rendering.internal.mixin;

import io.github.astrarre.rendering.v0.api.textures.SpriteInfo;
import io.github.astrarre.rendering.v0.api.textures.Texture;
import io.github.astrarre.rendering.v0.api.textures.TexturePart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;

@Mixin(Sprite.class)
public abstract class SpriteMixin implements SpriteInfo {

	@Shadow @Final private SpriteAtlasTexture atlas;

	@Shadow @Final private int[] frameXs;

	@Shadow @Final private int[] frameYs;

	@Shadow @Final protected NativeImage[] images;

	private int atlasWidth, atlasHeight;
	@Inject(method = "<init>", at = @At("RETURN"))
	public void onInit(SpriteAtlasTexture spriteAtlasTexture,
			Sprite.Info info,
			int maxLevel,
			int atlasWidth,
			int atlasHeight,
			int x,
			int y,
			NativeImage nativeImage,
			CallbackInfo ci) {
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
	}

	@Override
	public TexturePart getTexture(int frame) {
		int x = this.frameXs[frame], y = this.frameYs[frame];
		NativeImage image = this.images[frame];
		return new TexturePart(new Texture(this.atlas.getId(), this.atlasWidth, this.atlasHeight), x, y, image.getWidth(), image.getHeight());
	}

	@Override
	public int frames() {
		return this.frameXs.length;
	}
}
