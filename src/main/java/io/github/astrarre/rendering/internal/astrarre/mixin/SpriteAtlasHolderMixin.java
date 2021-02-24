package io.github.astrarre.rendering.internal.astrarre.mixin;

import java.util.function.Consumer;
import java.util.stream.Stream;

import io.github.astrarre.rendering.v0.api.SpriteManager;
import io.github.astrarre.rendering.v0.api.textures.SpriteInfo;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.v0.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.util.Identifier;

@Mixin (SpriteAtlasHolder.class)
public abstract class SpriteAtlasHolderMixin implements SpriteManager {
	@Override
	public void forEach(Consumer<SpriteInfo> consumer) {
		this.getSprites().map(Id.class::cast).map(this::getSprite).map(SpriteInfo.class::cast).forEach(consumer);
	}

	@Shadow
	protected abstract Stream<Identifier> getSprites();

	@Shadow
	protected abstract net.minecraft.client.texture.Sprite shadow$getSprite(Identifier objectId);

	@Override
	public SpriteInfo getSprite(Id sprite) {
		return (SpriteInfo) this.shadow$getSprite(sprite.to());
	}

}