package io.github.astrarre.gui.internal.mixin;

import java.util.function.Consumer;
import java.util.stream.Stream;

import io.github.astrarre.gui.v0.api.textures.SpriteManager;
import io.github.astrarre.v0.client.texture.Sprite;
import io.github.astrarre.v0.util.Id;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.util.Identifier;

@Mixin (SpriteAtlasHolder.class)
public abstract class SpriteAtlasHolderMixin implements SpriteManager {
	@Shadow
	protected abstract net.minecraft.client.texture.Sprite shadow$getSprite(Identifier objectId);
	@Shadow
	protected abstract Stream<Identifier> getSprites();

	@Override
	public void forEach(Consumer<Sprite> consumer) {
		this.getSprites().map(Id.class::cast).map(this::getSprite).map(Sprite.class::cast).forEach(consumer);
	}

	@Override
	public Sprite getSprite(Id sprite) {
		return (Sprite) this.shadow$getSprite((Identifier) sprite);
	}
}
