package io.github.astrarre.rendering.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.SpriteAtlasManager;

@Mixin (BakedModelManager.class)
public interface BakedModelManagerAccess {
	@Accessor
	SpriteAtlasManager getAtlasManager();
}
