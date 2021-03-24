package io.github.astrarre.rendering.internal.mixin;

import java.util.Collection;
import java.util.Map;

import io.github.astrarre.rendering.internal.textures.SpriteAtlasManagerManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

@Mixin(SpriteAtlasManager.class)
public class SpriteAtlasManagerMixin {
	@Shadow @Final private Map<Identifier, SpriteAtlasTexture> atlases;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void inject(Collection<SpriteAtlasTexture> collection, CallbackInfo ci) {
		SpriteAtlasManagerManager.MANAGER.forEach((identifier, manager) -> this.atlases.put(identifier, manager.getTexture()));
	}
}
