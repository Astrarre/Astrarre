package io.github.astrarre.gui.internal.mixin;

import java.util.Map;
import java.util.function.Consumer;

import io.github.astrarre.gui.v0.api.textures.SpriteManager;
import io.github.astrarre.v0.client.texture.Sprite;
import io.github.astrarre.v0.util.Id;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

@Mixin (SpriteAtlasTexture.class)
public class SpriteAtlasTextureMixin implements SpriteManager {
	@Shadow @Final private Map<Identifier, net.minecraft.client.texture.Sprite> sprites;

	@Override
	public void forEach(Consumer<Sprite> consumer) {
		for (net.minecraft.client.texture.Sprite value : this.sprites.values()) {
			consumer.accept((Sprite) value);
		}
	}

	@Override
	public Sprite getSprite(Id sprite) {
		return (Sprite) this.sprites.get(sprite);
	}
}
