package io.github.astrarre.recipe.internal.mixin;

import io.github.astrarre.recipe.v0.fabric.RecipePostReloadEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.profiler.Profiler;

@Mixin(SinglePreparationResourceReloadListener.class)
public abstract class SinglePreperationResourceReloadListenerMixin_RecipePostReloadEvent implements ResourceReloadListener {
	@Inject(method = "method_18790", at = @At("RETURN"))
	public void onPostReload(ResourceManager manager, Profiler profiler, Object object, CallbackInfo info) {
		if((Object)this instanceof RecipeManager) {
			RecipePostReloadEvent.EVENT.get().onReload((RecipeManager) (Object) this, ((RecipeManagerAccess)this).getRecipes());
		}
	}
}
