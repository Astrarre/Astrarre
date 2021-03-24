package io.github.astrarre.rendering.internal.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

@Mixin (SpriteAtlasManager.class)
public interface SpriteAtlasManagerAccess {
	@Accessor
	Map<Identifier, SpriteAtlasTexture> getAtlases();
}
