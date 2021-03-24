package io.github.astrarre.rendering.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.client.texture.SpriteAtlasTexture;

@Mixin (SpriteAtlasHolder.class)
public interface SpriteAtlasHolderAccess {
	@Accessor
	SpriteAtlasTexture getAtlas();
}
