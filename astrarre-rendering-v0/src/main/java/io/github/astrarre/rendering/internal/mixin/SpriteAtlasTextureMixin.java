package io.github.astrarre.rendering.internal.mixin;

import java.util.Map;
import java.util.function.Consumer;

import io.github.astrarre.rendering.v0.api.SpriteManager;
import io.github.astrarre.rendering.v0.api.textures.SpriteInfo;
import io.github.astrarre.util.v0.api.Id;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

@Mixin (SpriteAtlasTexture.class)
public class SpriteAtlasTextureMixin implements SpriteManager {
	@Shadow @Final private Map<Identifier, net.minecraft.client.texture.Sprite> sprites;

	@Override
	public void forEach(Consumer<SpriteInfo> consumer) {
		for (net.minecraft.client.texture.Sprite value : this.sprites.values()) {
			consumer.accept((SpriteInfo) value);
		}
	}

	@Override
	public SpriteInfo getSprite(Id sprite) {
		return (SpriteInfo) this.sprites.get(sprite);
	}
}
