package io.github.astrarre.rendering.internal.mixin;

import java.util.function.Consumer;
import java.util.stream.Stream;

import io.github.astrarre.rendering.v0.api.textures.client.SpriteManager;
import io.github.astrarre.rendering.v0.api.textures.client.ManagedSprite;
import io.github.astrarre.util.v0.api.Id;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.util.Identifier;

@Mixin (SpriteAtlasHolder.class)
public abstract class SpriteAtlasHolderMixin implements SpriteManager {
	@Override
	public void forEach(Consumer<ManagedSprite> consumer) {
		this.getSprites().map(Id.class::cast).map(this::getSprite).map(ManagedSprite.class::cast).forEach(consumer);
	}

	@Shadow
	protected abstract Stream<Identifier> getSprites();

	@Shadow
	protected abstract net.minecraft.client.texture.Sprite shadow$getSprite(Identifier objectId);

	@Override
	public ManagedSprite getSprite(Id sprite) {
		return (ManagedSprite) this.shadow$getSprite(sprite.to());
	}

}
