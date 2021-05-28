package io.github.astrarre.recipe.internal.mixin;

import io.github.astrarre.recipe.v0.fabric.RecipePostReloadEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.profiler.Profiler;

@Mixin(SinglePreparationResourceReloader.class)
public abstract class SinglePreperationResourceReloadListenerMixin_RecipePostReloadEvent implements ResourceReloader {
	@Inject(method = "method_18790", at = @At("RETURN"))
	public void onPostReload(ResourceManager manager, Profiler profiler, Object object, CallbackInfo info) {
		if((Object)this instanceof RecipeManager) {
			RecipePostReloadEvent.EVENT.get().onReload((RecipeManager) (Object) this, ((RecipeManagerAccess)this).getRecipes());
		}
	}
}
